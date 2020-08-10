package gulimall.order.to;

import gulimall.order.entity.OrderEntity;
import gulimall.order.entity.OrderItemEntity;
import lombok.Data;

import java.util.List;

/**
 * 创建的订单数据
 * @author 孙启新
 * <br>FileName: OrderCreateTo
 * <br>Date: 2020/08/10 13:01:32
 */
@Data
public class OrderCreateTo {
    /**
     * 订单
     */
    private OrderEntity orderEntity;
    /**
     * 订单项
     */
    private List<OrderItemEntity> orderItemEntities;
}
