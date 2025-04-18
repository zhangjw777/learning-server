package com.learning.aichat.Service;

import com.alibaba.dashscope.app.Application;
import com.alibaba.dashscope.app.ApplicationParam;
import com.alibaba.dashscope.app.ApplicationResult;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.learning.aichat.Controller.ChatBotController;
import io.reactivex.Flowable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class BaiLianAPIService {
    private static final Logger log = LoggerFactory.getLogger(ChatBotController.class);
    private static final Gson gson = new Gson(); // Gson 是线程安全的，可以复用


    // 注入配置好的线程池 Bean
    @Autowired
    @Qualifier("chatbotThreadPoolExecutor") // 指定 Bean 名称
    private ExecutorService executor; // 使用 ExecutorService 接口，更通用
    // 从配置文件注入 App ID
    @Value("${aliyun.dashscope.app-id}")
    private String appId;


    /**
     * 调用百炼应用，封装流式返回数据 (SSE 格式)
     *
     * @param emitter ResponseBodyEmitter 实例
     * @param query   用户输入的提示
     * @throws NoApiKeyException      如果 API Key 未配置或无效
     * @throws InputRequiredException 如果输入参数缺失（如 prompt 或 appId）
     */
    private void streamCall(ResponseBodyEmitter emitter, String query) throws NoApiKeyException, InputRequiredException {
        // 使用注入的 appId
        ApplicationParam param = ApplicationParam.builder()
                .appId(this.appId)
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .prompt(query)
                .incrementalOutput(true) // 保持增量输出
                .build();
        log.info("APIKey={}", System.getenv("DASHSCOPE_API_KEY"));
        Application application = new Application(); // DashScope SDK 的 Application 实例通常是轻量级的
        Flowable<ApplicationResult> result = application.streamCall(param);

        AtomicInteger counter = new AtomicInteger(0);

        // 使用 blockingForEach 处理流式结果
        // 注意：blockingForEach 会阻塞执行此任务的线程，直到流完成或出错
        result.blockingForEach(data -> {
            try {
                int newValue = counter.incrementAndGet();
                // 保持原始的 SSE 格式
                String sseData = String.format("id:%d\nevent:result\n:HTTP_STATUS/200\ndata:%s\n\n",
                        newValue,
                        gson.toJson(data)); // 使用共享的 Gson 实例

                emitter.send(sseData.getBytes(StandardCharsets.UTF_8), MediaType.TEXT_EVENT_STREAM);
                log.trace("Sent SSE chunk #{}: {}", newValue, sseData.replace("\n", "\\n")); // 记录发送的数据（避免日志过长，替换换行符）

                // 检查结束标志
                if (data.getOutput() != null && "stop".equals(data.getOutput().getFinishReason())) {
                    log.info("Received 'stop' finish reason. Completing SSE stream for prompt: {}", query);
                    emitter.complete();
                }
            } catch (IOException e) {
                // 当客户端断开连接时，send 操作会抛出 IOException
                log.warn("Failed to send data to client (client likely disconnected) for prompt [{}]: {}", query, e.getMessage());
                // 抛出异常，让 RxJava 的错误处理机制捕获，并中断 blockingForEach
                throw new RuntimeException("Failed to send data to client", e);
            } catch (Exception e) {
                log.error("Error processing or sending DashScope data chunk for prompt [{}]: {}", query, e.getMessage(), e);
                // 尝试发送错误信息给客户端（可能失败）并完成 emitter
                try {
                    emitter.send(("event:error\ndata:{\"error\":\"Processing error: " + e.getMessage() + "\"}\n\n")
                            .getBytes(StandardCharsets.UTF_8), MediaType.TEXT_EVENT_STREAM);
                } catch (IOException ignored) {
                } // 忽略发送错误时的异常
                emitter.completeWithError(e);
                // 抛出异常以中断 blockingForEach
                throw new RuntimeException("Error processing data chunk", e);
            }
        });

        // 如果 blockingForEach 正常完成但 emitter 仍未关闭 (例如 SDK 没有发送 finish_reason='stop')，确保关闭
        if (!isEmitterCompleted(emitter)) {
            log.warn("Flowable completed but emitter was not closed (finish_reason 'stop' likely missing). Completing emitter for prompt: {}", query);
            emitter.complete();
        }
    }


    // 辅助方法检查 Emitter 是否已完成 (避免重复调用 complete/completeWithError)
    // 注意：ResponseBodyEmitter 没有直接的 isCompleted 方法，这是一个基于反射的技巧，可能不稳定
    // 更稳妥的方式是使用标志位，但这里为了简洁暂时这样写
    private boolean isEmitterCompleted(ResponseBodyEmitter emitter) {
        try {
            // 这是一个 HACK，依赖内部实现，可能在未来版本失效
            java.lang.reflect.Field completedField = emitter.getClass().getDeclaredField("completed");
            completedField.setAccessible(true);
            return completedField.getBoolean(emitter);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.warn("Could not determine emitter completion status via reflection.", e);
            // 无法确定时，保守地认为它可能未完成，但这可能导致重复调用 complete/completeWithError
            // 在实际生产中，最好用 AtomicBoolean 等方式追踪状态
            return false;
        }
    }

    public ResponseBodyEmitter streamChat(String queryJson, ResponseBodyEmitter emitter) {
        String prompt;
        // 1. 解析输入参数
        try {
            JsonObject jsonObject = JsonParser.parseString(queryJson).getAsJsonObject();
            if (jsonObject.has("prompt") && jsonObject.get("prompt").isJsonPrimitive()) {
                prompt = jsonObject.get("prompt").getAsString();
                if (prompt == null || prompt.trim().isEmpty()) {
                    throw new IllegalArgumentException("Prompt cannot be empty.");
                }
            } else {
                throw new IllegalArgumentException("Invalid request body: missing or invalid 'prompt' field.");
            }
        } catch (JsonParseException | IllegalStateException | IllegalArgumentException e) {
            log.error("Failed to parse request JSON or invalid prompt: {}", queryJson, e);
            // 对于解析错误，直接返回错误可能比创建 Emitter 更好，但这里遵循原逻辑风格
            emitter.completeWithError(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request body: " + e.getMessage(), e));
            return emitter; // 返回已完成错误的 Emitter
        }

        // 2. 异步执行 DashScope 调用
        try {
            executor.execute(() -> {
                try {
                    log.debug("Submitting prompt to DashScope App ID [{}]: {}", appId, prompt);
                    streamCall(emitter, prompt);
                } catch (NoApiKeyException e) {
                    log.error("DashScope API Key not found or invalid. Ensure DASHSCOPE_API_KEY environment variable is set correctly.", e);
                    emitter.completeWithError(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "DashScope API Key configuration error.", e));
                } catch (InputRequiredException e) {
                    log.error("DashScope input required exception for prompt: {}", prompt, e);
                    // 这通常意味着 prompt 为空或 appId 错误，但我们前面已检查 prompt
                    emitter.completeWithError(new ResponseStatusException(HttpStatus.BAD_REQUEST, "DashScope input error: " + e.getMessage(), e));
                } catch (Exception e) { // 捕获其他潜在异常
                    log.error("Error during DashScope stream call for prompt: {}", prompt, e);
                    emitter.completeWithError(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error during AI processing.", e));
                }
            });
        } catch (RejectedExecutionException e) {
            log.error("Task rejected by thread pool. Pool might be overloaded. Prompt: {}", prompt, e);
            emitter.completeWithError(new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Server busy, please try again later.", e));
        } catch (Exception e) {
            log.error("Failed to submit task to executor for prompt: {}", prompt, e);
            emitter.completeWithError(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to initiate AI processing.", e));
        }

        // 3. 设置 Emitter 完成和超时的回调
        emitter.onCompletion(() -> log.info("SSE stream completed for prompt: {}", prompt));
        emitter.onTimeout(() -> {
            log.warn("SSE stream timed out for prompt: {}", prompt);
            emitter.complete(); // 超时也需要调用 complete
        });
        emitter.onError(throwable -> log.error("Error occurred in SSE stream for prompt: {}", prompt, throwable)); // 主要记录由 completeWithError 触发的错误

        log.info("SSE emitter created for prompt: {}", prompt);
        return emitter;
    }

}
