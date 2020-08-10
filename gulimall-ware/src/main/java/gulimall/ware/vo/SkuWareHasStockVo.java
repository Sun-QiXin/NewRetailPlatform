package gulimall.ware.vo;

import java.io.Serializable;
import java.util.List;

/**
 * 哪个商品在哪些仓库拥有库存
 * @author 孙启新
 * <br>FileName: SkuWareHasStock
 * <br>Date: 2020/08/10 16:33:21
 */
public class SkuWareHasStockVo implements Serializable {
    /**
     * 商品Id
     */
    private Long skuId;
    /**
     * 锁定商品的数量
     */
    private Integer num;
    /**
     * 仓库列表
     */
    private List<Long> wareIds;

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public List<Long> getWareIds() {
        return wareIds;
    }

    public void setWareIds(List<Long> wareIds) {
        this.wareIds = wareIds;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}
