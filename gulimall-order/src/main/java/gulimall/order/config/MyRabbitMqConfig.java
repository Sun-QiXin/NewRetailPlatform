package gulimall.order.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * RabbitMQ配置类
 *
 * @author 孙启新
 * <br>FileName: MyRabbitMqConfig
 * <br>Date: 2020/08/07 14:41:44
 */
@Configuration
public class MyRabbitMqConfig {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 将默认序列化机制设置json
     *
     * @return Jackson2JsonMessageConverter
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 自定义RabbitTempLate
     * <br>1、服务收到消息就回调
     * <br>1)spring.rabbitmq.publisher-confirm-type: simple
     * <br>2)设置确认回调confirmCallback
     * <br>2、消息正确抵达队列进行回调
     * <br>1)spring.rabbitmq.publisher-returns=true
     * <br>2)spring.rabbitmq.tempLate.mandatory=true
     * <br>@PostConstruct,在MyRabbitMqConfig对象创建完成后执行这个方法
     */
    @PostConstruct
    public void initRabbitTemplate() {
        //1、设置服务收到消息就回调
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             * 服务器收到消息就回调
             * @param correlationData 当前消息的唯一关联数据（包含消息唯一id)
             * @param ack 消息是否成功收到
             * @param cause 失败的原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                System.out.println(correlationData);
                System.out.println(ack);
                System.out.println(cause);
            }
        });

        //2、消息正确抵达队列进行回调
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            /**
             * 消息没有到达指定的队列触发
             * @param message    投递失败的消息信息
             * @param replyCode  回复码
             * @param replyText  回复的文本内容
             * @param exchange   当时消息发给哪个交换机
             * @param routingKey 当时消息发给哪个交换机时的路由键
             */
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                System.out.println(message);
            }
        });
    }
}
