package cn.linter.learning.file.config;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Minio配置
 *
 * @author 张家伟
 * @date 2025/04/04
 */
@Slf4j
@Configuration
public class MinioConfig {

    @Value("${minio.endpoint}")
    private String endPoint;
    @Value("${minio.access-key}")
    private String accessKey;
    @Value("${minio.secret-key}")
    private String secretKey;

    @Bean
    public MinioClient minioClientFactory() {
        log.info("开始创建 MinioClient Bean, endpoint: {}", endPoint);
        try {
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(endPoint)
                    .credentials(accessKey, secretKey)
                    .build();
            log.info("MinioClient Bean 创建成功！");
            return minioClient;
        } catch (Exception e) {
            log.error("创建 MinioClient Bean 失败: {}", e.getMessage(), e);
            // 这里可以根据实际情况决定是否抛出异常中断程序启动
            // throw new RuntimeException("无法创建 MinioClient Bean", e);
            return null; // 或者返回 null，并在使用时进行检查
        }
    }

}
