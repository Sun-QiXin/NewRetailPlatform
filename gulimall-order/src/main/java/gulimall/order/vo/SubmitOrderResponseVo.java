package gulimall.order.vo;

import gulimall.order.entity.OrderEntity;
import lombok.Data;

/**
 * 下单操作响应vo
 * @author 孙启新
 * <br>FileName: SubmitOrderResponseVo
 * <br>Date: 2020/08/10 11:54:25
 */
@Data
public class SubmitOrderResponseVo {
    /**
     * 订单信息
     */
    private OrderEntity orderEntity;
    /**
     * 错误状态码，0成功
     */
    private Integer code;
}
