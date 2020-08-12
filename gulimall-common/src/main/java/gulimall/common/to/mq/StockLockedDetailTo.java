package gulimall.common.to.mq;

import lombok.Data;

/**
 * @author 孙启新
 * <br>FileName: StockLockedDetailTo
 * <br>Date: 2020/08/12 14:15:30
 */
@Data
public class StockLockedDetailTo {
    /**
     * id
     */
    private Long id;
    /**
     * sku_id
     */
    private Long skuId;
    /**
     * sku_name
     */
    private String skuName;
    /**
     * 购买个数
     */
    private Integer skuNum;
    /**
     * 工作单id
     */
    private Long taskId;
    /**
     * 仓库id
     */
    private Long wareId;
    /**
     * 1-锁定   2-解锁   3-扣减
     */
    private Integer lockStatus;
}
