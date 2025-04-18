package com.learning.file.service.impl;

import com.learning.file.service.FileService; // 导入自定义的文件服务接口
import io.minio.*; // 导入Minio核心类，如MinioClient
import io.minio.errors.*; // 导入Minio可能抛出的各种异常类
import org.springframework.beans.factory.annotation.Value; // 导入Spring的@Value注解，用于注入配置文件中的值
import org.springframework.stereotype.Service; // 导入Spring的@Service注解，标记这是一个服务类Bean
import org.springframework.web.multipart.MultipartFile; // 导入Spring MVC的MultipartFile接口，用于处理文件上传
import org.springframework.util.StringUtils;
import java.io.IOException; // 导入Java的IO异常
import java.security.InvalidKeyException; // 导入Java安全相关的异常
import java.security.NoSuchAlgorithmException; // 导入Java安全相关的异常
import java.util.Objects; // 导入Java工具类，用于对象操作，如非空检查
import java.util.UUID; // 导入Java工具类，用于生成唯一标识符

/**
 * 文件服务实现类
 * <p>
 * 这个类实现了 FileService 接口中定义的文件操作方法。
 * 它使用 Minio 作为对象存储后端，负责处理文件的上传，
 * 并确保目标存储桶（Bucket）存在且具有正确的访问策略。
 * </p>
 *
 * @author 张家伟
 * @date 2025/04/04
 */
@Service // @Service注解表明这个类是Spring容器管理的一个服务组件（Bean）
public class FileServiceImpl implements FileService {

    // final关键字确保minioClient实例在初始化后不会被改变
    // 这是与Minio服务器交互的核心客户端对象
    private final MinioClient minioClient;

    // 使用@Value注解从Spring Boot的配置文件(application.properties或application.yml)中注入值
    // ${gateway.address} 表示获取名为 "gateway.address" 的配置项的值
    // 这个地址通常是用户访问上传后文件的网关或反向代理地址
    @Value("${gateway.address}")
    private String gatewayAddress;

    // 注入用于存储视频文件的Minio存储桶（Bucket）的名称
    @Value("${minio.video-bucket-name}")
    private String videoBucketName;

    // 注入用于存储封面图片的Minio存储桶（Bucket）的名称
    @Value("${minio.cover-picture-bucket-name}")
    private String coverPictureBucketName;

    // 注入用于存储用户头像的Minio存储桶（Bucket）的名称
    @Value("${minio.profile-picture-bucket-name}")
    private String profilePictureBucketName;

    /**
     * 构造函数注入 MinioClient 实例。
     * Spring Boot会自动查找一个类型为 MinioClient 的Bean（通常在配置类中创建）并传入。
     * 这是推荐的依赖注入方式（相比于 @Autowired 字段注入）。
     *
     * @param minioClient Minio客户端实例
     */
    public FileServiceImpl(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    /**
     * 上传视频文件。
     *
     * @param multipartFile 从HTTP请求中接收到的视频文件对象
     * @return 可公开访问的视频文件URL
     * @throws IOException              当读取文件流或网络通信时发生IO错误
     * @throws InvalidKeyException      当使用的Minio访问密钥或密钥无效时
     * @throws InvalidResponseException 当Minio服务器返回无效响应时
     * @throws InsufficientDataException 当提供的数据不足以完成操作时
     * @throws NoSuchAlgorithmException 当请求的加密或签名算法不可用时
     * @throws ServerException          当Minio服务器内部发生错误时
     * @throws InternalException        当Minio客户端库内部发生错误时
     * @throws XmlParserException       当解析Minio返回的XML响应时发生错误
     * @throws ErrorResponseException   当Minio服务器返回一个错误响应（如权限不足）时
     */
    @Override // 表明这个方法是实现了父接口 FileService 中的方法
    public String uploadVideo(MultipartFile multipartFile) throws IOException, InvalidKeyException, InvalidResponseException,
            InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, ErrorResponseException {
        // 调用通用的文件保存方法，并指定使用配置的视频存储桶名称
        return saveMultipartFileWithRandomName(multipartFile, videoBucketName);
    }

    /**
     * 上传封面图片文件。
     *
     * @param multipartFile 从HTTP请求中接收到的封面图片文件对象
     * @return 可公开访问的封面图片URL
     * @throws IOException              (同上)
     * @throws InvalidKeyException      (同上)
     * @throws InvalidResponseException (同上)
     * @throws InsufficientDataException (同上)
     * @throws NoSuchAlgorithmException (同上)
     * @throws ServerException          (同上)
     * @throws InternalException        (同上)
     * @throws XmlParserException       (同上)
     * @throws ErrorResponseException   (同上)
     */
    @Override
    public String uploadCoverPicture(MultipartFile multipartFile) throws IOException, InvalidKeyException, InvalidResponseException,
            InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, ErrorResponseException {
        // 调用通用的文件保存方法，并指定使用配置的封面图片存储桶名称
        return saveMultipartFileWithRandomName(multipartFile, coverPictureBucketName);
    }

    /**
     * 上传用户头像文件。
     *
     * @param multipartFile 从HTTP请求中接收到的用户头像文件对象
     * @return 可公开访问的用户头像URL
     * @throws IOException              (同上)
     * @throws InvalidKeyException      (同上)
     * @throws InvalidResponseException (同上)
     * @throws InsufficientDataException (同上)
     * @throws NoSuchAlgorithmException (同上)
     * @throws ServerException          (同上)
     * @throws InternalException        (同上)
     * @throws XmlParserException       (同上)
     * @throws ErrorResponseException   (同上)
     */
    @Override
    public String uploadProfilePicture(MultipartFile multipartFile) throws IOException, InvalidKeyException, InvalidResponseException,
            InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, ErrorResponseException {
        // 调用通用的文件保存方法，并指定使用配置的用户头像存储桶名称
        return saveMultipartFileWithRandomName(multipartFile, profilePictureBucketName);
    }

    /**
     * 将 MultipartFile 保存到指定的 Minio 存储桶中，并使用UUID生成随机文件名。
     *
     * @param multipartFile 要保存的文件对象
     * @param bucketName    目标Minio存储桶的名称
     * @return 可公开访问的文件URL
     * @throws IOException              (同上)
     * @throws InvalidKeyException      (同上)
     * @throws InvalidResponseException (同上)
     * @throws InsufficientDataException (同上)
     * @throws NoSuchAlgorithmException (同上)
     * @throws ServerException          (同上)
     * @throws InternalException        (同上)
     * @throws XmlParserException       (同上)
     * @throws ErrorResponseException   (同上)
     */
    private String saveMultipartFileWithRandomName(MultipartFile multipartFile, String bucketName) throws IOException, InvalidKeyException, InvalidResponseException,
            InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, ErrorResponseException {

        // 1. 确保目标存储桶存在，如果不存在则创建并设置公共读策略
        makeBucket(bucketName);

        // 2. 获取上传文件的原始文件名 (例如: "my_video.mp4")
        String fileName = multipartFile.getOriginalFilename();
        // 优化建议: 对 fileName 进行更健壮的空值和路径检查，虽然 Objects.requireNonNull 已经处理了 null 的情况。

         String originalFilename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
         if (originalFilename == null || originalFilename.isEmpty()) { throw new IllegalArgumentException("上传文件名不能为空"); }

        // 3. 提取文件的扩展名 (例如: "mp4")
        // Objects.requireNonNull 确保 fileName 不为 null，如果为 null 会抛出 NullPointerException
        // lastIndexOf(".") 找到最后一个点的位置
        // substring() 截取从最后一个点之后一位到末尾的字符串
        String fileType = Objects.requireNonNull(fileName).substring(fileName.lastIndexOf(".") + 1);
        // 优化建议: 这里假设文件名总是有扩展名且格式规范。可以增加对没有扩展名或文件名以"."开头等情况的处理。
        // 例如: int dotIndex = originalFilename.lastIndexOf('.');
        // String fileType = (dotIndex == -1 || dotIndex == originalFilename.length() - 1) ? "" : originalFilename.substring(dotIndex + 1);

        // 4. 生成一个随机的UUID作为新的文件名（不包含扩展名），以避免文件名冲突
        String randomName = UUID.randomUUID().toString();

        // 5. 构造最终在Minio中存储的对象名称 (例如: "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx.mp4")
        String objectName = randomName + "." + fileType;

        // 6. 使用 MinioClient 的 putObject 方法上传文件
        minioClient.putObject(
                PutObjectArgs.builder() // 使用构建者模式创建上传参数对象
                        .bucket(bucketName) // 指定要上传到的存储桶名称
                        .object(objectName) // 指定文件在存储桶中的名称（包含路径和文件名）
                        .stream(multipartFile.getInputStream(), multipartFile.getSize(), -1)
                        // .stream() 方法用于上传文件流
                        // 第一个参数: multipartFile.getInputStream() 获取上传文件的输入流
                        // 第二个参数: multipartFile.getSize() 获取上传文件的大小（字节）
                        // 第三个参数: -1 表示对象大小未知或者你想让Minio自动分块上传（对于大文件推荐，通常设置为-1或不设置让其自动处理，或者设置一个较大的分块大小如 10485760L (10MB)）
                        //            注意: Spring Boot 2.x 的 MultipartFile.getSize() 通常能正确获取大小。
                        .contentType(multipartFile.getContentType()) // 设置文件的MIME类型 (例如: "video/mp4", "image/jpeg")，这有助于浏览器正确处理文件
                        .build() // 构建最终的参数对象
        );
        // 优化建议: 考虑添加上传进度监听器或更详细的错误处理/日志记录。

        // 7. 拼接并返回可公开访问的文件URL
        // 格式通常是: 网关地址 + 固定路径(如/dfs/) + 存储桶名称 + 对象名称
        // 例如: "http://your-gateway.com/dfs/videos/xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx.mp4"
        return gatewayAddress + "/dfs/" + bucketName + "/" + objectName;
        // 优化建议: 这个URL的拼接方式强依赖于网关的路由配置。确保网关配置了 `/dfs/{bucketName}/{objectName...}` 这样的路由转发到Minio。
        // 也可以考虑使用Minio的预签名URL功能（`getPresignedObjectUrl`），如果文件不需要永久公开访问。
    }

    /**
     * 检查指定的存储桶是否存在，如果不存在，则创建该存储桶并设置公共读取策略。
     * 这确保了上传的文件可以通过URL被公开访问。
     *
     * @param bucketName 要检查或创建的存储桶名称
     * @throws IOException              (同上)
     * @throws InvalidKeyException      (同上)
     * @throws InvalidResponseException (同上)
     * @throws InsufficientDataException (同上)
     * @throws NoSuchAlgorithmException (同上)
     * @throws ServerException          (同上)
     * @throws InternalException        (同上)
     * @throws XmlParserException       (同上)
     * @throws ErrorResponseException   (同上)
     */
    private void makeBucket(String bucketName) throws IOException, InvalidKeyException, InvalidResponseException,
            InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, ErrorResponseException {

        // 1. 检查存储桶是否存在
        boolean isBucketExist = minioClient.bucketExists(
                BucketExistsArgs.builder() // 使用构建者模式创建检查参数
                        .bucket(bucketName) // 指定要检查的存储桶名称
                        .build() // 构建参数对象
        );

        // 2. 如果存储桶不存在
        if (!isBucketExist) {
            // 2.1 创建存储桶
            minioClient.makeBucket(
                    MakeBucketArgs.builder() // 使用构建者模式创建创建参数
                            .bucket(bucketName) // 指定要创建的存储桶名称
                            .build() // 构建参数对象
            );
            // 优化建议: 创建存储桶的操作可能因为并发请求而失败（如果另一个请求同时创建）。可以添加重试逻辑或捕获特定异常。

            // 2.2 定义一个允许匿名用户读取存储桶内所有对象的策略 (JSON格式)
            // 这个策略允许任何人 (*) 执行 s3:GetObject 操作（即读取文件）
            // 对于存储桶本身，允许 s3:GetBucketLocation 和 s3:ListBucket 操作（通常也是公开需要的）
            // Resource 指定了策略应用的范围：存储桶本身 (`arn:aws:s3:::` + bucketName) 和 存储桶内的所有对象 (`arn:aws:s3:::` + bucketName + `/*`)
            // Version 是策略语言的版本号，通常是 "2012-10-17"
            String policy = "{\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucket\"],\"Resource\":[\"arn:aws:s3:::" + bucketName + "\"]}," +
                    "{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetObject\"],\"Resource\":[\"arn:aws:s3:::" + bucketName + "/*\"]}],\"Version\":\"2012-10-17\"}";
            // 优化建议: 将策略JSON字符串存储在配置文件或常量中，而不是硬编码在代码里。
            // 也可以考虑使用更精细的权限控制，而不是完全公开读。

            // 2.3 将定义好的策略应用到新创建的存储桶上
            minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder() // 使用构建者模式创建设置策略参数
                            .bucket(bucketName) // 指定目标存储桶
                            .config(policy) // 设置策略内容 (JSON字符串)
                            .build() // 构建参数对象
            );
        }
        // 优化建议: 即使存储桶已存在，其策略也可能被意外更改。可以考虑每次上传前都检查并设置策略，但这会增加API调用次数。
        // 或者定期检查策略的正确性。对于大多数场景，仅在创建时设置一次是足够的。
    }

    // 整体优化建议:
    // 1. 异常处理: 当前方法签名抛出了大量具体的Minio异常。可以考虑在Service层捕获这些具体异常，
    //    然后包装成自定义的业务异常（例如 FileUploadException），这样Controller层处理起来更简单。
    // 2. 配置管理: 将硬编码的 "/dfs/" 路径前缀、策略JSON等提取到配置文件中。
    // 3. 文件类型/大小限制: 可以在上传前增加对文件类型和大小的校验。
    // 4. 日志记录: 在关键步骤（如开始上传、上传成功、发生错误）添加日志记录，有助于调试和监控。
    // 5. 异步处理: 对于非常大的文件上传，可以考虑将其放入消息队列进行异步处理，避免长时间阻塞HTTP请求线程。
}