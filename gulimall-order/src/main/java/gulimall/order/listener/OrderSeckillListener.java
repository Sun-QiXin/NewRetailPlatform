package gulimall.order.listener;

import com.rabbitmq.client.Channel;
import gulimall.common.to.mq.SeckillOrderTo;
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
 * 监听秒杀队列
 * @author 孙启新
 * <br>FileName: OrderSeckillListener
 * <br>Date: 2020/08/19 15:16:22
 */
@Component
@RabbitListener(queues = MyRabbitMqConfig.ORDER_SECKILL_QUEUE)
public class OrderSeckillListener {
    @Autowired
    private OrderService orderService;

    /**
     * 监听RabbitMQ的秒杀订单消息，进行正常逻辑，创建订单，扣库存等等
     *
     * @param message       message
     * @param seckillOrderTo seckillOrderTo
     * @param channel       channel
     * @throws IOException IOException
     */
    @RabbitHandler
    public void listenerOrderClose(Message message, SeckillOrderTo seckillOrderTo, Channel channel) throws IOException {
        try {
            System.out.println("收到RabbitMQ秒杀订单消息-----------------");
            orderService.createSeckillOrder(seckillOrderTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
            e.printStackTrace();
        }
    }
}
