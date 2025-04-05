package cn.linter.learning.gateway.config;

import cn.linter.learning.common.entity.ResultStatus; // 导入了自定义的 ResultStatus 类，通常用于封装统一的 API 响应状态（如错误码、消息）。
import com.fasterxml.jackson.core.JsonProcessingException; // 导入 Jackson 库的异常类，用于处理将 Java 对象序列化为 JSON 字符串时可能发生的错误。
import com.fasterxml.jackson.databind.ObjectMapper; // 导入 Jackson 库的核心类，用于 Java 对象和 JSON 数据之间的转换（序列化和反序列化）。
import lombok.extern.slf4j.Slf4j; // 导入 Lombok 提供的日志注解，可以方便地创建一个 SLF4J 的 Logger 实例，变量名为 log。
import org.springframework.context.annotation.Bean; // 导入 Spring 框架的注解，用于声明一个方法，该方法的返回值将作为一个 Bean 注册到 Spring 应用上下文中。
import org.springframework.context.annotation.Configuration; // 导入 Spring 框架的注解，表明这个类是一个配置类，Spring 容器会处理这个类，通常与 @Bean 注解一起使用。
import org.springframework.core.convert.converter.Converter; // 导入 Spring 框架的转换器接口，用于类型转换。这里用于将 JWT 转换为认证对象。
import org.springframework.core.io.buffer.DataBuffer; // 导入 Spring WebFlux 提供的类，用于处理非阻塞的字节数据流。
import org.springframework.http.HttpMethod; // 导入 Spring 框架的枚举，表示 HTTP 请求方法（GET, POST, PUT, DELETE 等）。
import org.springframework.http.HttpStatus; // 导入 Spring 框架的枚举，表示 HTTP 状态码（如 200 OK, 401 Unauthorized, 403 Forbidden 等）。
import org.springframework.http.MediaType; // 导入 Spring 框架的类，表示 HTTP 的媒体类型（如 application/json, text/html 等）。
import org.springframework.http.server.reactive.ServerHttpResponse; // 导入 Spring WebFlux 提供的接口，代表一个响应式（非阻塞）的 HTTP 服务器响应。
import org.springframework.security.authentication.AbstractAuthenticationToken; // 导入 Spring Security 的抽象类，是各种认证令牌（Authentication Token）的基础实现。
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity; // 导入 Spring Security 的注解，用于启用 WebFlux 环境下的 Spring Security 功能。这是整合 Spring Cloud Gateway 和 Spring Security 的关键注解。
import org.springframework.security.config.web.server.ServerHttpSecurity; // 导入 Spring Security 的类，用于配置 WebFlux 环境下的 HTTP 安全性，类似于 Servlet 环境下的 HttpSecurity。
import org.springframework.security.oauth2.jwt.Jwt; // 导入 Spring Security OAuth2 提供的类，代表一个已解码的 JSON Web Token (JWT)。
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter; // 导入 Spring Security OAuth2 提供的类，用于将 JWT 转换为 Spring Security 的 Authentication 对象（包含用户信息和权限）。
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter; // 导入 Spring Security OAuth2 提供的类，用于从 JWT 的声明（claims）中提取权限（GrantedAuthority）。
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter; // 导入 Spring Security OAuth2 提供的适配器类，将标准的 JwtAuthenticationConverter 适配给响应式（Reactive）环境使用。
import org.springframework.security.web.server.SecurityWebFilterChain; // 导入 Spring Security 的接口，代表一个安全过滤器链，在 WebFlux 环境下处理请求的安全逻辑。
import org.springframework.web.server.ServerWebExchange; // 导入 Spring WebFlux 提供的接口，代表一次完整的 HTTP 请求-响应交互过程。
import reactor.core.publisher.Mono; // 导入 Project Reactor 框架的核心类，代表一个包含 0 或 1 个元素的异步序列。在 WebFlux 中广泛用于处理异步操作。

import java.nio.charset.StandardCharsets; // 导入 Java 标准库的类，定义了标准的字符集，如 UTF-8。

/**
 * Security配置
 * 这个类负责配置 Spring Cloud Gateway 的安全策略，
 * 使用 Spring Security 和 OAuth2 Resource Server 来保护 API 接口。
 * 它定义了哪些路径需要认证，哪些路径可以公开访问，以及如何处理认证和授权失败的情况。
 *
 * @author 张家伟
 * @since 2025/04/04
 */
@Slf4j // Lombok 注解，自动为这个类创建一个名为 log 的 SLF4J Logger 实例，方便记录日志。
@Configuration // 声明这是一个 Spring 配置类。Spring 启动时会扫描并加载这个类中的 Bean 定义。
@EnableWebFluxSecurity // 启用 Spring Security 对 WebFlux 应用的支持。因为 Spring Cloud Gateway 是基于 WebFlux 的，所以必须使用这个注解。
public class WebFluxSecurityConfig {

    // 声明一个 ObjectMapper 实例，用于后续将 Java 对象（如错误信息）转换为 JSON 字符串。
    // final 关键字表示这个引用在对象创建后不能被修改。
    private final ObjectMapper objectMapper;

    // 构造函数注入 ObjectMapper。
    // Spring 会自动查找一个已经注册在容器中的 ObjectMapper Bean，并将其传递给这个构造函数。
    // 这是推荐的依赖注入方式。
    public WebFluxSecurityConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 配置核心的安全过滤器链 Bean。
     * 这个方法定义了整个网关的安全规则。
     * 除了路径匹配规则以外，其他大量的都为固定模板
     *
     * @param http Spring Security 提供的 ServerHttpSecurity 对象，用于构建安全配置。
     * @return SecurityWebFilterChain Bean，包含了所有的安全规则配置。
     */
    @Bean // 将这个方法返回的 SecurityWebFilterChain 对象注册为一个 Spring Bean。
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                // 开始配置路径授权规则
                .authorizeExchange()
                // 对所有路径的 OPTIONS 请求放行。
                // OPTIONS 请求通常用于浏览器的 CORS（跨域资源共享）预检请求，检查服务器是否允许实际的跨域请求。
                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // 对 /api/oauth/token 路径放行。
                // 这个路径通常是 OAuth2 获取访问令牌（Access Token）的端点，需要公开访问。
                .pathMatchers("/api/oauth/token").permitAll()
                // 对 POST 方法访问 /api/oauth/user 路径放行。
                // 这个路径通常用于用户注册，也需要公开访问。
                .pathMatchers(HttpMethod.POST, "/api/oauth/user").permitAll()
                // 对 GET 方法访问 /api/oauth/user/** （如 /api/oauth/user/123）路径要求进行身份认证。
                // 只有携带有效凭证（如 JWT）的用户才能访问这些路径。
                .pathMatchers(HttpMethod.GET, "/api/oauth/user/**").authenticated()
                // 对任何其他未明确匹配的请求也放行。
                // 注意：这可能是一个宽松的设置。在生产环境中，通常建议默认拒绝所有未明确允许的请求（.anyExchange().denyAll() 或 .anyExchange().authenticated()），然后显式放行需要的路径。
                // 在网关场景下，有时会设置为 permitAll()，依赖下游微服务自行进行更细粒度的权限控制。
                .anyExchange().permitAll()
                // .and() 方法用于连接不同的配置部分
                .and()
                // 开始配置异常处理
                .exceptionHandling()
                // 配置认证入口点（Authentication Entry Point）。
                // 当一个未经身份验证的用户尝试访问需要认证的资源时，会调用这个处理器。
                // 这里使用 lambda 表达式自定义了处理逻辑：调用 sendRestResponse 方法返回一个 JSON 格式的 401 Unauthorized 错误。
                .authenticationEntryPoint((exchange, exception) -> sendRestResponse(exchange,
                        HttpStatus.UNAUTHORIZED, ResultStatus.UNAUTHORIZED)
                )
                // 配置访问拒绝处理器（Access Denied Handler）。
                // 当一个已经通过身份验证的用户尝试访问他们没有权限的资源时，会调用这个处理器。
                // 这里使用 lambda 表达式自定义了处理逻辑：调用 sendRestResponse 方法返回一个 JSON 格式的 403 Forbidden 错误。
                .accessDeniedHandler((exchange, exception) -> sendRestResponse(exchange,
                        HttpStatus.FORBIDDEN, ResultStatus.FORBIDDEN)
                )
                // .and() 方法返回 ServerHttpSecurity 对象，继续配置
                .and()
                // 禁用 CSRF (Cross-Site Request Forgery) 保护。
                // 对于基于 Token（如 JWT）的无状态认证 API，CSRF 保护通常是不必要的，因为客户端每次请求都需要携带 Token，
                // 并且 Token 不会像 Cookie 一样被浏览器自动发送，从而天然地防御了 CSRF 攻击。
                .csrf().disable()
                // 配置 OAuth2 资源服务器（Resource Server）支持。
                // 这表明该网关服务将作为资源服务器，能够接收并验证 OAuth2 令牌（这里特指 JWT）。
                .oauth2ResourceServer(oauth2 -> oauth2
                        // 配置 JWT（JSON Web Token）相关的处理
                        .jwt(jwt -> jwt
                                // 设置自定义的 JWT 转换器。
                                // 这个转换器负责将传入的 JWT 解析，并提取用户信息（Principal）和权限（Authorities），
                                // 转换成 Spring Security 内部使用的 Authentication 对象。
                                .jwtAuthenticationConverter(getJwtAuthenticationConverter())
                        )
                        // 配置资源服务器自身的认证入口点。
                        // 这个处理器在 OAuth2 认证过程中发生错误时（例如 Token 无效、过期、格式错误）被调用。
                        // 注意：这与上面配置的全局 authenticationEntryPoint 不同，这个更侧重于 OAuth2/JWT 验证失败的场景。
                        .authenticationEntryPoint((exchange, exception) -> sendRestResponse(exchange,
                                HttpStatus.UNAUTHORIZED, ResultStatus.TOKEN_IS_INVALID)
                        )
                );
        // 构建并返回 SecurityWebFilterChain 对象，完成配置。
        return http.build();
    }

    /**
     * 创建并配置 JWT 到 Authentication 对象的转换器。
     * 这个转换器定义了如何从 JWT 中提取用户权限信息。
     *
     * @return 一个配置好的 Converter，用于将 Jwt 转换为包含用户主体和权限的 AbstractAuthenticationToken。
     */
    public Converter<Jwt, Mono<AbstractAuthenticationToken>> getJwtAuthenticationConverter() {
        // 创建一个用于从 JWT 中提取权限的转换器
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        // 设置权限的前缀为空字符串。
        // 默认情况下，Spring Security 会给从 JWT scope 或自定义 claim 中提取的权限加上 "SCOPE_" 或 "ROLE_" 前缀。
        // 设置为空字符串表示直接使用 JWT 中声明的权限名称，不加任何前缀。
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
        // 指定从 JWT 的哪个声明（Claim）中获取权限列表。
        // 这里设置为 "authorities"，表示 JWT 中应该有一个名为 "authorities" 的 claim，其值是一个包含用户权限字符串的数组或列表。
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");

        // 创建一个标准的 JWT 认证转换器
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        // 将上面配置好的权限提取转换器设置给 JWT 认证转换器。
        // 这样，JWT 认证转换器就知道如何从 JWT 中解析出权限信息了。
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        // 因为我们是在 WebFlux 环境下（响应式编程模型），需要将标准的（非响应式）JwtAuthenticationConverter
        // 包装成一个适用于响应式环境的转换器。ReactiveJwtAuthenticationConverterAdapter 就是做这个适配工作的。
        // 它返回 Mono<AbstractAuthenticationToken>，符合 WebFlux 的异步处理要求。
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

    /**
     * 发送自定义的 RESTful 风格的错误响应。
     * 这个辅助方法用于统一处理认证和授权失败时的响应格式。
     *
     * @param exchange     当前的 HTTP 请求-响应交互对象，可以从中获取 Response 对象。
     * @param httpStatus   要设置的 HTTP 状态码 (如 401, 403)。
     * @param resultStatus 自定义的包含错误码和错误信息的对象。
     * @return 一个 Mono<Void>，表示一个完成信号，表明响应写入操作已安排执行。
     */
    private Mono<Void> sendRestResponse(ServerWebExchange exchange, HttpStatus httpStatus, ResultStatus resultStatus) {
        // 从 ServerWebExchange 中获取 HTTP 响应对象
        ServerHttpResponse httpResponse = exchange.getResponse();
        // 设置响应头的 Content-Type 为 application/json，告诉客户端响应体是 JSON 格式。
        httpResponse.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        // 设置 HTTP 响应状态码
        httpResponse.setStatusCode(httpStatus);
        // 准备响应体内容
        String body;
        try {
            // 使用 ObjectMapper 将自定义的 ResultStatus 对象序列化为 JSON 字符串。
            body = objectMapper.writeValueAsString(resultStatus);
        } catch (JsonProcessingException e) {
            // 如果 JSON 序列化失败，则将异常信息作为响应体，并记录错误日志。
            body = e.getMessage();
            log.error("Json process error", e); // 使用 @Slf4j 注入的 log 对象记录错误
        }
        // 将 JSON 字符串转换为字节数组，使用 UTF-8 编码。
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        // 使用响应对象的 bufferFactory 创建一个 DataBuffer（WebFlux 中用于处理字节流的对象），并包装字节数组。
        DataBuffer buffer = httpResponse.bufferFactory().wrap(bytes);
        // 将包含错误信息的 DataBuffer 写入响应体。
        // Mono.just(buffer) 创建一个只包含这个 buffer 的异步序列。
        // writeWith 方法接收一个 Publisher<DataBuffer>，并将发布的数据写入响应。
        // 返回一个 Mono<Void> 表示写入操作完成。
        return httpResponse.writeWith(Mono.just(buffer));
    }

}