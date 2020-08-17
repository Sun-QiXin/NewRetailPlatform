package gulimall.seckill.to;


import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * 商品的详细信息To
 * @author 孙启新
 * <br>FileName: SeckillSkuRedisTo
 * <br>Date: 2020/08/17 10:54:23
 */
@Data
public class SeckillSkuRedisTo implements Serializable {
    /**
     * id
     */
    private Long id;
    /**
     * 活动id
     */
    private Long promotionId;
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
     * 秒杀总量
     */
    private BigDecimal seckillCount;
    /**
     * 每人限购数量
     */
    private BigDecimal seckillLimit;
    /**
     * 排序
     */
    private Integer seckillSort;
    /**
     * 当前商品的开始时间
     */
    private Long startTime;
    /**
     * 当前商品的结束时间
     */
    private Long endTime;
    /**
     * 秒杀随机码
     */
    private String randomCode;
    /**
     * 商品的详细信息
     */
    private SkuInfoTo skuInfoTo;
}
