package gulimall.order.config;

import org.springframework.amqp.core.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * rabbitMq的队列交换机配置
 *
 * @author 孙启新
 * <br>FileName: MyRabbitMqConfig
 * <br>Date: 2020/08/12 11:07:22
 */
@Configuration
public class MyRabbitMqConfig {
    /**
     * 交换机名称
     */
    public static final String ORDER_EVENT_EXCHANGE = "order.event.exchange";
    /**
     * 延时队列名称
     */
    public static final String ORDER_DELAY_QUEUE = "order.delay.queue";
    /**
     * 死信队列名称
     */
    public static final String ORDER_DEAD_QUEUE = "order.dead.queue";
    /**
     * 路由到延时队列使用的路由键
     */
    public static final String ORDER_DELAY_KEY = "order.create.order";
    /**
     * 路由到死信队列使用的路由键
     */
    public static final String ORDER_DEAD_KEY = "order.dead.key";

    /**
     * 创建一个交换机
     *
     * @return 交换机
     */
    @Bean
    public Exchange orderEventExchange() {
        return ExchangeBuilder.topicExchange(ORDER_EVENT_EXCHANGE).durable(true).build();
    }

    /**
     * 创建一个延时队列(延时30分钟)
     *
     * @return 延时队列
     */
    @Bean
    public Queue orderDelayQueue() {
        return QueueBuilder.durable(ORDER_DELAY_QUEUE).ttl(60000 * 30).deadLetterExchange(ORDER_EVENT_EXCHANGE).deadLetterRoutingKey(ORDER_DEAD_KEY).build();
    }

    /**
     * 创建一个死信队列用于接收延时队列过期的消息
     *
     * @return 死信队列
     */
    @Bean
    public Queue orderDeadQueue() {
        return QueueBuilder.durable(ORDER_DEAD_QUEUE).build();
    }

    /**
     * 将交换机与延时队列绑定
     *
     * @return Binding
     */
    @Bean
    public Binding bindingDelayQueue() {
        return new Binding(ORDER_DELAY_QUEUE, Binding.DestinationType.QUEUE, ORDER_EVENT_EXCHANGE, ORDER_DELAY_KEY, null);
    }

    /**
     * 将交换机与死信队列绑定
     *
     * @return Binding
     */
    @Bean
    public Binding bindingDeadQueue() {
        return new Binding(ORDER_DEAD_QUEUE, Binding.DestinationType.QUEUE, ORDER_EVENT_EXCHANGE, ORDER_DEAD_KEY, null);
    }

    /**
     * 将订单服务的交换机和库存服务的死信队列绑定（出现网络不通畅等问题解锁库存比关闭订单慢了，那就可以使用该绑定再次发送一次消息，直接到达解锁库存）
     *
     * @return Binding
     */
    @Bean
    public Binding bindingWareDeadQueue() {
        return new Binding("ware.dead.queue", Binding.DestinationType.QUEUE, ORDER_EVENT_EXCHANGE, "ware.dead.#", null);
    }
}
