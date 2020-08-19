package gulimall.common.to.mq;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 秒杀订单To数据
 *
 * @author 孙启新
 * <br>FileName: SeckillOrderTo
 * <br>Date: 2020/08/19 14:43:20
 */
@Data
public class SeckillOrderTo {
    /**
     * 订单号
     */
    private String orderSn;
    /**
     * 活动场次id
     */
    private Long promotionSessionId;
    /**
     * 商品id
     */
    private Long skuId;
    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;
    /**
     * 秒杀数量
     */
    private Integer seckillCount;
    /**
     * 购买人id
     */
    private Long memberId;
    /**
     * 商品的详细信息
     */
    private SkuInfoTo skuInfoTo;
}
