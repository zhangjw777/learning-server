/*
package cn.linter.learning.file.service.impl;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor // Lombok 注解，为 final 字段生成构造函数，实现依赖注入
public class Demo {

    private final MinioClient minioClient; // 注入 MinioClient Bean

    @Value("${minio.bucketName}")
    private String bucketName;

    */
/**
     * 服务启动时检查存储桶是否存在，不存在则创建。
     *//*

    @PostConstruct // 在 Bean 初始化后执行
    public void initBucket() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("存储桶 '{}' 不存在，已自动创建。", bucketName);
            } else {
                log.info("存储桶 '{}' 已存在。", bucketName);
            }
        } catch (Exception e) {
            log.error("初始化 MinIO 存储桶 '{}' 时发生错误: {}", bucketName, e.getMessage(), e);
            // 根据需要处理异常，例如记录错误或通知管理员
        }
    }

    */
/**
     * 检查存储桶是否存在
     * @param bucket 存储桶名称
     * @return true 如果存在, false 如果不存在
     *//*

    public boolean bucketExists(String bucket) {
        try {
            return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        } catch (Exception e) {
            log.error("检查存储桶 '{}' 是否存在时出错: {}", bucket, e.getMessage(), e);
            return false; // 发生错误时，保守地认为它不存在或无法访问
        }
    }

    */
/**
     * 创建存储桶
     * @param bucket 存储桶名称
     * @return true 如果创建成功或已存在, false 如果创建失败
     *//*

    public boolean makeBucket(String bucket) {
        try {
            if (!bucketExists(bucket)) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                log.info("存储桶 '{}' 创建成功。", bucket);
            }
            return true;
        } catch (Exception e) {
            log.error("创建存储桶 '{}' 时出错: {}", bucket, e.getMessage(), e);
            return false;
        }
    }

    */
/**
     * 列出所有存储桶
     * @return 存储桶列表
     *//*

    public List<Bucket> listBuckets() {
        try {
            return minioClient.listBuckets();
        } catch (Exception e) {
            log.error("列出所有存储桶时出错: {}", e.getMessage(), e);
            return List.of(); // 返回空列表
        }
    }

    */
/**
     * 上传文件
     * @param file MultipartFile 文件对象
     * @return 上传成功后的文件名 (在 MinIO 中的对象名) 或 null (如果上传失败)
     *//*

    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            log.warn("尝试上传空文件。");
            return null;
        }

        // 生成唯一的文件名，避免重名覆盖
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        // 使用 UUID 生成唯一前缀，拼接原始文件扩展名
        String objectName = UUID.randomUUID().toString().replace("-", "") + fileExtension;

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName) // 目标存储桶
                            .object(objectName) // 在存储桶中的对象名称
                            .stream(inputStream, file.getSize(), -1) // 文件流、大小、分片大小(-1表示自动)
                            .contentType(file.getContentType()) // 设置文件的 Content-Type
                            .build()
            );
            log.info("文件 '{}' 上传成功，存储为 '{}'。", originalFilename, objectName);
            return objectName; // 返回在 MinIO 中存储的对象名
        } catch (Exception e) {
            log.error("上传文件 '{}' 到存储桶 '{}' 时出错: {}", originalFilename, bucketName, e.getMessage(), e);
            return null; // 上传失败返回 null
        }
    }

    */
/**
     * 下载文件
     * @param objectName MinIO 中的对象名
     * @return 文件的 InputStream，如果文件不存在或发生错误则返回 null
     *//*

    public InputStream downloadFile(String objectName) {
        try {
            GetObjectResponse response = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            log.info("成功获取文件 '{}' 的输入流。", objectName);
            // 注意：返回的是 InputStream，调用者需要负责关闭它
            return response;
        } catch (ErrorResponseException e) {
            // 特别处理文件不存在的错误 (NoSuchKey)
            if ("NoSuchKey".equals(e.errorResponse().code())) {
                log.warn("尝试下载的文件 '{}' 在存储桶 '{}' 中不存在。", objectName, bucketName);
            } else {
                log.error("下载文件 '{}' 时发生 MinIO 错误响应: {}", objectName, e.errorResponse(), e);
            }
            return null;
        }
        catch (Exception e) {
            log.error("下载文件 '{}' 时发生未知错误: {}", objectName, e.getMessage(), e);
            return null;
        }
    }

    */
/**
     * 获取文件信息 (StatObject)
     * @param objectName MinIO 中的对象名
     * @return StatObjectResponse 包含文件元数据，如果文件不存在或发生错误则返回 null
     *//*

    public StatObjectResponse getObjectInfo(String objectName) {
        try {
            return minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        } catch (ErrorResponseException e) {
            if ("NoSuchKey".equals(e.errorResponse().code())) {
                log.warn("尝试获取信息的文件 '{}' 在存储桶 '{}' 中不存在。", objectName, bucketName);
            } else {
                log.error("获取文件 '{}' 信息时发生 MinIO 错误响应: {}", objectName, e.errorResponse(), e);
            }
            return null;
        } catch (Exception e) {
            log.error("获取文件 '{}' 信息时发生未知错误: {}", objectName, e.getMessage(), e);
            return null;
        }
    }

    // 你还可以添加删除文件、生成预签名 URL 等其他方法...
    // public boolean deleteObject(String objectName) { ... }
    // public String getPresignedObjectUrl(String objectName, int expirySeconds) { ... }
}*/
