package gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 封装提交订单的数据
 * @author 孙启新
 * <br>FileName: OrderSubmitVo
 * <br>Date: 2020/08/10 11:08:17
 */
@Data
public class OrderSubmitVo {
    /**
     * 收货地址Id
     */
    private Long addressId;
    /**
     * 支付方式
     */
    private Integer payType;
    /**
     * 放重令牌
     */
    private String orderToken;
    /**
     * 应付价格
     */
    private BigDecimal payPrice;
    /**
     * 订单总额
     */
    private BigDecimal totalAmount;
    /**
     * 备注
     */
    private String note;
    /**
     * 会员积分抵扣金额
     */
    private BigDecimal integrationAmount;
}
