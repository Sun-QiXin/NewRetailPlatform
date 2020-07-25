package gulimall.common.to;

import lombok.Data;

/**
 * @author 孙启新
 * <br>FileName: SkuHasStockVo
 * <br>Date: 2020/07/25 15:55:18
 */
@Data
public class SkuHasStockVo {
    /**
     * skuId
     */
    private Long skuId;
    /**
     * 是否有库存
     */
    private Boolean hasStock;
}
