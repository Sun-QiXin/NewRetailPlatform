package gulimall.ware.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 哪个商品在哪些仓库拥有库存
 * @author 孙启新
 * <br>FileName: SkuWareHasStock
 * <br>Date: 2020/08/10 16:33:21
 */
@Data
public class SkuWareHasStockVo implements Serializable {
    /**
     * 商品Id
     */
    private Long skuId;
    /**
     * 商品名字
     */
    private String skuName;
    /**
     * 锁定商品的数量
     */
    private Integer num;
    /**
     * 仓库列表
     */
    private List<Long> wareIds;
}
