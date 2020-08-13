package gulimall.order.listener;

import com.rabbitmq.client.Channel;
import gulimall.order.config.MyRabbitMqConfig;
import gulimall.order.entity.OrderEntity;
import gulimall.order.service.OrderService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 监听RabbitMQ的订单消息
 *
 * @author 孙启新
 * <br>FileName: OrderCloseListener
 * <br>Date: 2020/08/13 09:48:51
 */
@Component
@RabbitListener(queues = MyRabbitMqConfig.ORDER_DEAD_QUEUE)
public class OrderCloseListener {
    @Autowired
    private OrderService orderService;

    /**
     * 监听RabbitMQ的订单消息，判断是否需要关闭订单
     *
     * @param message       message
     * @param orderEntity orderEntity
     * @param channel       channel
     * @throws IOException IOException
     */
    @RabbitHandler
    public void listenerOrderClose(Message message, OrderEntity orderEntity, Channel channel) throws IOException {
        try {
            System.out.println("收到RabbitMQ订单消息-----------------");
            orderService.closeOrder(orderEntity);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
            e.printStackTrace();
        }
    }
}
