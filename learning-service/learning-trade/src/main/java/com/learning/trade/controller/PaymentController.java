package com.learning.trade.controller;

// 导入我们项目中其他模块或库的类，这些类提供了所需的功能。
import com.learning.common.entity.Result; // 导入自定义的通用返回结果类，用于封装API响应数据。
import com.learning.common.entity.ResultStatus; // 导入自定义的返回状态枚举，例如成功、失败等。
import com.learning.common.exception.BusinessException; // 导入自定义的业务异常类，用于处理特定业务逻辑错误。
import com.learning.trade.client.CourseClient; // 导入用于与“课程服务”进行通信的客户端接口（可能使用了Feign等技术）。
import com.learning.trade.entity.Course; // 导入课程实体类，代表课程信息。
import com.learning.trade.entity.Order; // 导入订单实体类，代表订单信息。
import com.learning.trade.service.OrderService; // 导入订单服务接口，定义了处理订单相关的业务逻辑。
import com.alipay.easysdk.factory.Factory; // 导入支付宝Easy SDK的核心工厂类，用于初始化和获取支付能力。
import com.alipay.easysdk.payment.page.models.AlipayTradePagePayResponse; // 导入支付宝电脑网站支付的响应模型类。
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value; // 导入Spring框架的注解，用于从配置文件（如 application.properties 或 application.yml）中读取值并注入到变量中。
import org.springframework.web.bind.annotation.RequestMapping; // 导入Spring Web注解，用于映射HTTP请求到特定的处理方法。
import org.springframework.web.bind.annotation.RequestParam; // 导入Spring Web注解，用于从HTTP请求中提取参数。
import org.springframework.web.bind.annotation.RestController; // 导入Spring Web注解，这是一个组合注解，表明这个类是RESTful风格的控制器，并且其所有方法的返回值默认会被序列化为JSON或XML等格式。

import javax.servlet.http.HttpServletResponse; // 导入Java Servlet API中的类，用于直接操作HTTP响应。
import java.io.IOException; // 导入Java的IO异常类。
import java.io.PrintWriter; // 导入Java的类，用于向HTTP响应体中写入文本内容。

/**
 * 支付控制器 (Payment Controller)
 *
 * 这个类负责处理与支付相关的HTTP请求，例如发起支付和处理支付成功后的回调。
 * 它使用了支付宝的Easy SDK来简化与支付宝API的交互。
 *
 * @author 张家伟 (Original Author)
 * @since 2025/04/04 (Original Since Date)
 */
@Slf4j
@RestController // 声明这个类是一个REST控制器。Spring会自动扫描并注册它来处理网络请求。
@RequestMapping("payments") // 声明这个控制器处理的所有请求路径都以 "/payments" 开头。例如，"/payments/pay", "/payments/success"。
public class PaymentController {

    // 使用 @Value 注解从配置文件中读取网关服务的协议（如 "http" 或 "https"）。
    // 这个值将用于构建支付成功后的回调URL。
    @Value("${gateway.protocol}")
    private String gatewayProtocol;

    // 使用 @Value 注解从配置文件中读取网关服务的主机名或IP地址 "localhost:8080"
    // 这个值也将用于构建支付成功后的回调URL。
    @Value("${gateway.host}")
    private String gatewayHost;

    // 声明一个订单服务(OrderService)的实例变量。
    // final关键字表示这个变量在对象创建后不能被重新赋值。
    private final OrderService orderService;

    // 声明一个课程服务客户端(CourseClient)的实例变量。
    // 这个客户端用于和另一个可能独立运行的“课程”微服务进行通信。
    private final CourseClient courseClient;

    /**
     * 构造函数 (Constructor)
     * Spring Boot 会自动查找 OrderService 和 CourseClient 的实例（Bean）并注入到这里。
     * 这就是所谓的“构造函数注入”，是依赖注入（Dependency Injection, DI）的一种推荐方式。
     *
     * @param orderService 订单服务的实例。
     * @param courseClient 课程服务客户端的实例。
     */
    public PaymentController(OrderService orderService, CourseClient courseClient) {
        this.orderService = orderService; // 将注入的orderService实例赋值给类成员变量。
        this.courseClient = courseClient; // 将注入的courseClient实例赋值给类成员变量。
    }

    /**
     * 发起支付请求的处理方法。
     * 当用户访问 "/payments" 路径（通常是通过GET或POST请求，这里@RequestMapping没指定方法，默认都支持）
     * 并且URL中带有 "orderId" 参数时，这个方法会被调用。
     *
     * @param orderId 从请求参数中获取的订单ID。例如，请求URL可能是 /payments?orderId=123
     * @return 返回一个包含支付宝支付页面HTML内容的Result对象。前端可以直接将这个HTML渲染出来，引导用户跳转到支付宝支付。
     */
    @RequestMapping // 映射到控制器的根路径，即 "/payments"。
    public Result<String> pay(@RequestParam Long orderId) { // @RequestParam注解用于从请求URL的查询参数中获取orderId的值。
        // 1. 根据传入的 orderId，调用 orderService 查询对应的订单详细信息。
        Order order = orderService.queryById(orderId);
        // 如果找不到订单，orderService.queryById 内部应该会处理，例如抛出异常或返回null（当前代码未显式处理null情况）。

        // 声明一个支付宝页面支付响应对象变量。
        AlipayTradePagePayResponse pagePayResponse;
        try {
            // 2. 使用支付宝Easy SDK的工厂类来发起一个电脑网站支付请求。
            pagePayResponse = Factory.Payment.Page() // 获取页面支付能力实例。
                    .pay( // 调用支付方法。
                            order.getProductName(), // 参数1: 商品的标题/交易标题/订单标题/订单关键字等。支付宝页面上会展示。
                            order.getTradeNo(),     // 参数2: 商户订单号。必须是唯一的，用于标识这笔交易。支付宝用它来关联异步通知。
                            order.getPrice().toString(), // 参数3: 订单总金额。需要转换为字符串格式。单位为元，精确到小数点后两位。
                            // 参数4: 支付成功后的同步跳转页面 (Return URL)。
                            // 用户在支付宝支付成功后，支付宝会引导用户的浏览器跳转到这个URL。
                            // 注意：这个URL是*同步*跳转，仅用于给用户展示结果，不能完全依赖它来确认支付状态（用户可能中途关闭浏览器）。
                            // 真实的支付状态确认应依赖支付宝的*异步*通知 (Notify URL)。这段代码似乎没有处理异步通知。
                            gatewayProtocol + "://" + gatewayHost + "/api/payments/success" // 拼接完整的同步回调URL。
                    );
        } catch (Exception e) {
            // 3. 如果在调用支付宝SDK时发生任何错误（网络问题、配置错误、参数错误等）。
            // 捕获异常，并抛出一个自定义的业务异常，表示支付创建失败。
            // 使用自定义异常和状态码有助于统一错误处理。
            throw new BusinessException(ResultStatus.PAYMENT_CREATE_FAILURE);
        }

        // 4. 如果支付宝SDK调用成功，pagePayResponse会包含一段HTML代码。
        // 这段HTML通常是一个自动提交的表单，会将用户重定向到支付宝的支付页面。
        // 将这段HTML内容包装在自定义的Result对象中，并设置状态为成功，然后返回给前端。
        return Result.of(ResultStatus.SUCCESS, pagePayResponse.getBody());
    }

    /**
     * 处理支付宝支付成功后的同步回调请求。
     * 这个方法对应上面 `pay` 方法中设置的 `returnUrl`。
     * 当用户在支付宝页面完成支付后，支付宝会把用户的浏览器重定向到这个路径 ("/payments/success")。
     *
     * 重要提示：这是一个同步回调。它只表示用户在支付宝界面上操作成功了，
     * 但不能100%保证资金已经到账。网络延迟、用户关闭浏览器等都可能导致这个请求收不到。
     * 生产环境中，强烈建议依赖支付宝的【异步通知】(notify_url)来更新订单状态和处理后续业务逻辑，因为它更可靠。
     *
     * @param tradeNo 支付宝在回调时通过URL参数传回来的商户订单号 (out_trade_no)。
     * @param response HttpServletResponse对象，用于直接向用户的浏览器输出响应内容（这里是HTML）。
     */
    @RequestMapping("success") // 映射到 "/payments/success" 路径。
    public void paymentSuccess(@RequestParam("out_trade_no") String tradeNo, HttpServletResponse response) {
        // @RequestParam("out_trade_no") 表示从请求URL中获取名为 "out_trade_no" 的参数值，并赋给 tradeNo 变量。
        // 例如，回调URL可能是 /payments/success?out_trade_no=20250405123456789&total_amount=...&trade_no=...

        // 1. 根据支付宝传回来的 tradeNo (商户订单号)，查询我们的数据库中的订单信息。
        Order order = orderService.queryByTradeNo(tradeNo);
        // 同样，这里假设能找到订单。实际应用中需要处理找不到订单的情况。

        // 2. 更新订单状态。将订单状态设置为 1。
        // 这里的状态 1 通常表示 "已支付" 或 "支付成功"。
        order.setStatus(1);
        // 调用 orderService 将更新后的订单信息保存回数据库。
        orderService.update(order);

        // 3. 调用课程服务，处理与课程相关的逻辑。
        // 创建一个临时的 Course 对象，主要用于传递课程ID。
        Course course = new Course();
        course.setId(order.getProductId()); // 设置课程ID，这个ID是从订单信息中获取的。
        // course.setRegistered(true); // 这行代码在原始逻辑中设置了值但未使用，可以注释掉或移除。

        // 通过 courseClient 调用远程的课程服务接口。
        // 传递了课程ID (course.getId()) 和购买用户的用户名 (order.getUsername())。
        // 这个调用的目的很可能是：告知课程服务，该用户已经购买了该课程，需要将用户加入课程或更新用户的学习状态等。
        courseClient.updateCourse(course.getId(), order.getUsername());

        // 4. 向用户的浏览器直接输出HTML响应。
        // 设置响应内容的类型为 HTML，并指定字符编码为 UTF-8，防止中文乱码。
        response.setContentType("text/html; charset=utf8");
        try (PrintWriter out = response.getWriter()) { // 使用 try-with-resources 确保 PrintWriter 在使用后能被自动关闭。
            // 向响应中写入一段HTML。
            out.print("支付成功，3秒之后跳转回课程页面" + // 显示提示信息给用户。
                    "<script>setTimeout(()=>window.location.href=\"http://localhost:3000/courses/" + // 嵌入JavaScript代码。
                    order.getProductId() + "\",3000)</script>"); // 使用 setTimeout 函数，在3000毫秒（3秒）后，将用户的浏览器页面重定向到对应的课程详情页。
            // 注意：这里的 "http://localhost:3000" 是前端应用的地址，实际部署时应配置为可访问的地址。
        } catch (IOException e) {
            // 如果在写入响应时发生IO错误，打印错误堆栈信息到服务器日志。。
            log.error("IO 错误：{}", e.getMessage());
        }
    }

}