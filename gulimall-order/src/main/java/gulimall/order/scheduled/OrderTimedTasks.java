package gulimall.order.scheduled;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import gulimall.common.to.mq.OrderTo;
import gulimall.order.entity.MqMessageEntity;
import gulimall.order.entity.OrderEntity;
import gulimall.order.service.MqMessageService;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * 定时任务
 *
 * @author 孙启新
 * <br>FileName: OrderTimedTasks
 * <br>Date: 2020/08/13 15:51:24
 */
@Component
@EnableScheduling
public class OrderTimedTasks {
    @Autowired
    private MqMessageService mqMessageService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 每隔一段时间检索数据库，查看有没有发送失败的mq消息，如果有就重新发送
     */
    @Scheduled(fixedDelay = 60000 * 60)
    public void resendFailMessage() {
        //1、拿到所有状态为错误抵达的消息
        List<MqMessageEntity> mqMessageEntities = mqMessageService.list(new QueryWrapper<MqMessageEntity>().eq("message_status", 1));
        if (mqMessageEntities != null && mqMessageEntities.size() > 0) {
            for (MqMessageEntity mqMessageEntity : mqMessageEntities) {
                String classType = mqMessageEntity.getClassType();
                if ("gulimall.order.entity.OrderEntity".equals(classType)){
                    OrderEntity orderEntity = JSON.parseObject(mqMessageEntity.getContent(), OrderEntity.class);
                    //重新发送，指定新的id
                    rabbitTemplate.convertAndSend(mqMessageEntity.getToExchane(), mqMessageEntity.getRoutingKey(), orderEntity,new CorrelationData(UUID.randomUUID().toString()));
                    //将当前处理的消息状态改为已送达
                    mqMessageEntity.setMessageStatus(0);
                    mqMessageService.updateById(mqMessageEntity);
                }else if ("gulimall.common.to.mq.OrderTo".equals(classType)){
                    OrderTo orderTo = JSON.parseObject(mqMessageEntity.getContent(), OrderTo.class);
                    //重新发送，指定新的id
                    rabbitTemplate.convertAndSend(mqMessageEntity.getToExchane(), mqMessageEntity.getRoutingKey(), orderTo,new CorrelationData(UUID.randomUUID().toString()));
                    //将当前处理的消息状态改为已送达
                    mqMessageEntity.setMessageStatus(0);
                    mqMessageService.updateById(mqMessageEntity);
                }
            }
        }
    }
}
