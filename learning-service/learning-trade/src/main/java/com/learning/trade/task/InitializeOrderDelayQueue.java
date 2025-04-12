package cn.linter.learning.trade.task;

import cn.linter.learning.trade.entity.Order; // 导入订单实体类，它代表了一个订单的数据结构
import cn.linter.learning.trade.service.OrderService; // 导入订单服务接口，用于处理订单相关的业务逻辑（如查询、更新）
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments; // 导入 Spring Boot 应用启动参数的接口
import org.springframework.boot.ApplicationRunner; // 导入 Spring Boot 的 ApplicationRunner 接口。实现这个接口的类会在 Spring Boot 应用启动完成后执行 run 方法
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor; // 导入 Spring 提供的线程池执行器，用于异步执行任务
import org.springframework.stereotype.Component; // 导入 Spring 的 @Component 注解，表明这个类是一个 Spring 组件，会被 Spring 容器管理

import java.util.concurrent.DelayQueue; // 导入 Java 并发包中的 DelayQueue 类。这是一个特殊的阻塞队列，只有当元素的延迟时间到了，才能从队列中取出

/**
 * 初始化订单延时队列任务
 * <p>
 * 这个类的主要职责是：
 * 1. 在 Spring Boot 应用程序启动后，从数据库加载所有未支付的订单。
 * 2. 将这些未支付的订单放入一个延时队列 (DelayQueue) 中。
 * 3. 启动一个后台线程，持续监控这个延时队列。
 * 4. 当队列中有订单的延迟时间到达（意味着订单超时未支付），后台线程会取出该订单，并将其状态更新为已取消（或超时关闭）。
 * </p>
 *
 * @author 张家伟
 * @since 2025/04/04
 */
@Slf4j
@Component // @Component 注解：告诉 Spring 框架，这个类需要被 Spring 创建和管理。
// Spring 会自动扫描并实例化这个类的对象（称为 Bean）。
public class InitializeOrderDelayQueue implements ApplicationRunner {

    // 声明一个线程池任务执行器。用于在后台异步执行任务，避免阻塞主线程。
    // final 关键字表示这个变量在对象创建后不能被重新赋值。
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    // 声明一个延时队列，专门存放 Order 对象。
    // DelayQueue 的特性是：放入其中的元素必须实现 Delayed 接口，并且只有当元素的 getDelay() 方法返回值小于等于 0 时，才能通过 take() 方法取出。
    // 这非常适合处理“XX时间后执行”的场景，比如订单超时未支付自动取消。
    private final DelayQueue<Order> orderDelayQueue;

    // 声明一个订单服务接口的实例。
    // 这个服务类封装了与订单数据相关的操作，如下面的 listUnpaid() 和 update()。
    private final OrderService orderService;

    /**
     * 构造函数。
     * Spring 框架会使用这个构造函数来创建 InitializeOrderDelayQueue 的实例。
     * 这是一种常见的依赖注入方式，称为“构造函数注入”。
     * Spring 会自动查找并传入所需的 Bean（ThreadPoolTaskExecutor, DelayQueue<Order>, OrderService 的实例）。
     * 这些依赖的 Bean 需要在其他地方（比如配置类）被定义和创建。
     *
     * @param threadPoolTaskExecutor Spring 容器注入的线程池执行器实例。
     * @param orderDelayQueue        Spring 容器注入的订单延时队列实例。
     * @param orderService           Spring 容器注入的订单服务实例。
     */
    public InitializeOrderDelayQueue(ThreadPoolTaskExecutor threadPoolTaskExecutor,
                                     DelayQueue<Order> orderDelayQueue,
                                     OrderService orderService) {
        // 将 Spring 注入的实例赋值给本类的成员变量。
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
        this.orderDelayQueue = orderDelayQueue;
        this.orderService = orderService;
    }

    /**
     * 实现 ApplicationRunner 接口的 run 方法。
     * 这个方法会在 Spring Boot 应用程序完全启动之后，由 Spring 自动调用一次。
     *
     * @param args 应用程序启动时传递的参数，这里没有使用到。
     */
    @Override
    public void run(ApplicationArguments args) {
        // 1. 初始化：加载未支付订单并放入延时队列
        // 调用 orderService 的 listUnpaid() 方法，获取所有状态为“未支付”的订单列表。
        // Order 类实现了 Delayed 接口，并且其 getDelay() 方法会根据订单创建时间和超时时间计算剩余延迟。
        // 将获取到的所有未支付订单批量添加到 orderDelayQueue 中。
        orderDelayQueue.addAll(orderService.listUnpaid());

        // 2. 启动后台处理线程
        // 使用注入的 threadPoolTaskExecutor 来执行一个新的任务。
        // 这个任务是一个 Lambda 表达式定义的 Runnable 对象。
        // execute() 方法会从线程池中获取一个线程来异步执行这个任务，这样就不会阻塞当前的应用启动流程。
        threadPoolTaskExecutor.execute(() -> {
            // 这个 Lambda 表达式定义了后台线程要执行的操作。
            // 使用一个无限循环 `while (true)`，表示这个线程会一直运行，持续处理队列中的订单，直到应用程序停止。
            while (true) {
                try {
                    // 3. 从延时队列中获取到期的订单
                    // 调用 orderDelayQueue.take() 方法。这是一个阻塞方法：
                    // - 如果队列中有元素的延迟时间已到（getDelay() <= 0），它会立即返回队列头部的那个元素。
                    // - 如果队列为空，或者所有元素的延迟时间都未到，该方法会一直等待，直到有元素到期或者线程被中断。
                    Order order = orderDelayQueue.take(); // 获取到超时的订单

                    // 4. 处理超时的订单
                    // 将取出的订单状态设置为 2。这里假设状态码 2 代表“已取消”或“已关闭”。
                    order.setStatus(2);

                    // 调用 orderService 的 update() 方法，将修改后的订单信息（主要是状态）更新到数据库或其他持久化存储中。
                    orderService.update(order);

                    // 可以在这里添加日志记录，方便追踪哪些订单被自动取消了。
                     log.info("订单 {} 超时未支付，已自动取消。", order.getId());
                } catch (InterruptedException e) {
                    // 5. 处理中断异常
                    // 当队列正在 take() 阻塞等待时，如果执行这个任务的线程被外部中断（例如，应用程序关闭时线程池被shutdown），
                    // take() 方法会抛出 InterruptedException。
                    // 这里只是简单地打印了异常堆栈信息。在生产环境中，可能需要更完善的处理逻辑，
                    // 比如记录日志、尝试重新设置中断状态 (Thread.currentThread().interrupt()) 或根据应用关闭流程决定是否退出循环。
                    log.error("订单延时队列处理任务被中断：{}", e.getMessage());
                    // 如果希望在中断时停止任务，可以取消注释下一行
                    // Thread.currentThread().interrupt(); // 重新设置中断状态
                    // break; // 或者直接跳出 while 循环
                } catch (Exception e) {
                    // 捕获其他可能的运行时异常，防止线程意外终止
                    // 记录错误日志非常重要
                     log.error("处理超时订单时发生异常: {}", e.getMessage(), e);
                }
            }
        });

        // run 方法执行完毕，但上面通过线程池启动的后台任务会继续运行。
        // 可以在这里添加一条日志，表明初始化和后台任务启动成功。
        // log.info("订单延时队列初始化完成，后台处理任务已启动。");
    }

}