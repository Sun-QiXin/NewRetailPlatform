package gulimall.ware.listener;

import com.alibaba.fastjson.TypeReference;
import com.rabbitmq.client.Channel;
import gulimall.common.enume.OrderStatusEnum;
import gulimall.common.to.mq.StockLockedDetailTo;
import gulimall.common.to.mq.StockLockedTo;
import gulimall.common.utils.R;
import gulimall.ware.config.MyRabbitMqConfig;
import gulimall.ware.entity.WareOrderTaskDetailEntity;
import gulimall.ware.entity.WareOrderTaskEntity;
import gulimall.ware.feign.OrderFeignService;
import gulimall.ware.service.WareOrderTaskDetailService;
import gulimall.ware.service.WareOrderTaskService;
import gulimall.ware.service.WareSkuService;
import gulimall.ware.vo.OrderVo;
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
    private OrderFeignService orderFeignService;

    @Autowired
    private WareSkuService wareSkuService;

    @Autowired
    private WareOrderTaskDetailService wareOrderTaskDetailService;

    @Autowired
    private WareOrderTaskService wareOrderTaskService;


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
        System.out.println("收到解锁库存得消息");
        try {
            StockLockedDetailTo lockedDetailTo = stockLockedTo.getLockedDetailTo();
            //1、查询数据库看有没有关于这个这个订单的锁定库存信息
            //有：查询订单,如果没有这个订单，然后再判断当前订单的状态（创建，发货。。）
            //没有：库存锁定失败，导致数据回滚，无须解锁
            WareOrderTaskDetailEntity taskDetailEntity = wareOrderTaskDetailService.getById(lockedDetailTo.getId());
            if (taskDetailEntity != null) {
                //获取当前订单的状态
                WareOrderTaskEntity wareOrderTaskEntity = wareOrderTaskService.getById(stockLockedTo.getTaskId());
                String orderSn = wareOrderTaskEntity.getOrderSn();
                //远程查询订单状态
                R r = orderFeignService.getOrder(orderSn);
                OrderVo orderVo = r.getData(new TypeReference<OrderVo>() {
                });
                //订单被取消或者订单不存在，解锁库存
                if (orderVo == null || OrderStatusEnum.CANCLED.getCode().equals(orderVo.getStatus())) {
                    //判断当前工作单的状态，锁定状态（1）的工作单才需要解锁
                    if (lockedDetailTo.getLockStatus() == 1) {
                        wareSkuService.unLockStock(lockedDetailTo);
                    }
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                } else {
                    //拒绝该消息，重新放回队列，等待下次消费
                    channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
                }
            } else {
                //无须解锁,确认即可
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }
        } catch (Exception e) {
            //如果出现异常，重新放回队列，等待下次消费
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            e.printStackTrace();
        }
    }
}
