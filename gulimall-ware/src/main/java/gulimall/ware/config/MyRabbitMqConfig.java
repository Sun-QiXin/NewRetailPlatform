package gulimall.ware.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * rabbitMq的队列交换机配置
 * @author 孙启新
 * <br>FileName: MyRabbitMqConfig
 * <br>Date: 2020/08/12 11:07:22
 */
@Configuration
public class MyRabbitMqConfig {
    /**
     * 交换机名称
     */
    public static final String WARE_EVENT_EXCHANGE = "ware_event_exchange";
    /**
     * 延时队列名称
     */
    public static final String WARE_DELAY_QUEUE= "ware_delay_queue";
    /**
     * 死信队列名称
     */
    public static final String WARE_DEAD_QUEUE= "ware_dead_queue";
    /**
     * 路由到延时队列使用的路由键
     */
    public static final String WARE_DELAY_KEY= "ware_locked";
    /**
     * 路由到死信队列使用的路由键
     */
    public static final String WARE_DEAD_KEY = "ware_dead.#";

    /**
     * 创建一个交换机
     * @return 交换机
     */
    @Bean
    public Exchange wareEventExchange(){
        return ExchangeBuilder.topicExchange(WARE_EVENT_EXCHANGE).durable(true).build();
    }

    /**
     * 创建一个延时队列
     * @return 延时队列
     */
    @Bean
    public Queue wareDelayQueue(){
        return QueueBuilder.durable(WARE_DELAY_QUEUE).ttl(120000).deadLetterExchange(WARE_EVENT_EXCHANGE).deadLetterRoutingKey(WARE_DEAD_KEY).build();
    }

    /**
     * 创建一个死信队列用于接收延时队列过期的消息
     * @return 死信队列
     */
    @Bean
    public Queue wareDeadQueue(){
        return QueueBuilder.durable(WARE_DEAD_QUEUE).build();
    }

    /**
     * 将交换机与延时队列绑定
     * @return Binding
     */
    @Bean
    public Binding bindingDelayQueue(){
        return new Binding(WARE_DELAY_QUEUE, Binding.DestinationType.QUEUE,WARE_EVENT_EXCHANGE,WARE_DELAY_KEY,null);
    }

    /**
     * 将交换机与死信队列绑定
     * @return Binding
     */
    @Bean
    public Binding bindingDeadQueue(){
        return new Binding(WARE_DEAD_QUEUE, Binding.DestinationType.QUEUE,WARE_EVENT_EXCHANGE,WARE_DEAD_KEY,null);
    }
}
