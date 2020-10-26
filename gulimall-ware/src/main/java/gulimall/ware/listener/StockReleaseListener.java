package gulimall.ware.listener;


import com.rabbitmq.client.Channel;
import gulimall.common.to.mq.OrderTo;
import gulimall.common.to.mq.StockLockedTo;
import gulimall.ware.config.MyRabbitMqConfig;
import gulimall.ware.service.WareSkuService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 监听rabbitMq解锁库存消息
 *
 * @author 孙启新
 * <br>FileName: StockReleaseListener
 * <br>Date: 2020/08/12 16:51:55
 */
@Component
@RabbitListener(queues = MyRabbitMqConfig.WARE_DEAD_QUEUE)
public class StockReleaseListener {
    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 监听库存解锁队列的消息，判断是否需要解锁
     *
     * @param message       message
     * @param stockLockedTo stockLockedTo
     * @param channel       channel
     * @throws IOException IOException
     */
    @RabbitHandler
    public void handleStockLockedRelease(Message message, StockLockedTo stockLockedTo, Channel channel) throws IOException {
        System.out.println("收到RabbitMQ解锁库存得消息--------------");
        try {
            wareSkuService.unLockStock(stockLockedTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            //如果出现异常，重新放回队列，等待下次消费
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            e.printStackTrace();
        }
    }


    /**
     * 监听由订单服务关闭订单直接发送到库存解锁队列的消息，判断是否需要解锁
     * <br>防止网络延迟等问题导致库存服务解锁库存时关闭订单被阻塞或没执行完查询一直是待付款状态，库存一直解锁不了
     *
     * @param message       message
     * @param orderTo orderTo
     * @param channel       channel
     * @throws IOException IOException
     */
    @RabbitHandler
    public void handleOrderCloseStockLockedRelease(Message message, OrderTo orderTo, Channel channel) throws IOException {
        System.out.println("收到由订单服务关闭订单直接发送到库存解锁队列的消息--------------");
        try {
            wareSkuService.unLockStock(orderTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            //如果出现异常，重新放回队列，等待下次消费
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            e.printStackTrace();
        }
    }
}
