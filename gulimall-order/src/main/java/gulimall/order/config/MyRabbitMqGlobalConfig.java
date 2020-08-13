package gulimall.order.config;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import gulimall.order.entity.MqMessageEntity;
import gulimall.order.service.MqMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Date;

/**
 * RabbitMQ全局配置
 *
 * @author 孙启新
 * <br>FileName: MyRabbitMqGlobalConfig
 * <br>Date: 2020/08/07 14:41:44
 */
@Configuration
@Slf4j
public class MyRabbitMqGlobalConfig {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MqMessageService mqMessageService;

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
     * 自定义RabbitTempLate,保证消息的可靠性,将失败的消息记录到数据库,定时任务重发
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
                System.out.println("RabbitMQ是否成功收到消息：" + ack);
                if (ack && correlationData != null) {
                    try {
                        //服务器收到消息，将该消息的状态保存进数据库
                        MqMessageEntity mqMessageEntity = new MqMessageEntity();
                        mqMessageEntity.setCreateTime(new Date());
                        mqMessageEntity.setMessageStatus(0);
                        mqMessageEntity.setMessageId(correlationData.getId());
                        mqMessageService.save(mqMessageEntity);
                    } catch (Exception e) {
                        log.error("该消息已经存在，messageId为：" + correlationData.getId());
                    }
                }
            }
        });

        //2、消息没有到达指定的队列回调
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            /**
             * 消息没有到达指定的队列触发，这个回调如果被触发会比confirm回调先触发
             * @param message    投递失败的消息信息
             * @param replyCode  回复码
             * @param replyText  回复的文本内容
             * @param exchange   当时消息发给哪个交换机
             * @param routingKey 当时消息发给哪个交换机时的路由键
             */
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                //消息id
                String messageId = (String) message.getMessageProperties().getHeaders().get("spring_returned_message_correlation");
                //消息类型
                String messageType = (String) message.getMessageProperties().getHeaders().get("__TypeId__");
                //消息内容
                String messageContent = new String(message.getBody());
                //更新消息状态，后面启用定时任务重发该消息
                MqMessageEntity mqMessageEntity = new MqMessageEntity();
                mqMessageEntity.setMessageStatus(1);
                mqMessageEntity.setCreateTime(new Date());
                mqMessageEntity.setMessageId(messageId);
                mqMessageEntity.setClassType(messageType);
                mqMessageEntity.setToExchane(exchange);
                mqMessageEntity.setRoutingKey(routingKey);
                mqMessageEntity.setContent(messageContent);
                mqMessageService.saveOrUpdate(mqMessageEntity, new UpdateWrapper<MqMessageEntity>().eq("message_id", messageId));
            }
        });
    }
}
