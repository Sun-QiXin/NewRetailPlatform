package gulimall.seckill.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 孙启新
 * <br>FileName: SeckillSkuRelationVo
 * <br>Date: 2020/08/17 10:12:11
 */
@Data
public class SeckillSkuRelationVo {

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
}
