package com.learning.aichat.Controller;

import com.learning.aichat.Service.BaiLianAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;


@RestController
public class ChatBotController {

    // SSE 超时时间 (毫秒)
    private static final Long SSE_TIMEOUT = 180_000L; // 3 分钟
    @Autowired
    private BaiLianAPIService baiLianAPIService;

    /**
     * 实现 chat 接口，支持流式返回数据 (SSE)
     *
     * @param queryJson 包含 "prompt" 字段的 JSON 字符串
     * @return ResponseBodyEmitter 用于 SSE 流式输出
     */
    @PostMapping(value = "/chat", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseBodyEmitter streamChat(@RequestBody String queryJson) {
        ResponseBodyEmitter emitter = new ResponseBodyEmitter(SSE_TIMEOUT);
        emitter = baiLianAPIService.streamChat(queryJson, emitter);
        return emitter;
    }


}
