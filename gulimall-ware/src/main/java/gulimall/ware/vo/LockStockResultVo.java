package gulimall.ware.vo;

/**
 * 锁定库存是否成功的结果
 * @author 孙启新
 * <br>FileName: LockStockResultVo
 * <br>Date: 2020/08/10 16:14:46
 */
public class LockStockResultVo {
    /**
     * 商品Id
     */
    private Long skuId;
    /**
     * 锁定数量
     */
    private Integer num;
    /**
     * 是否锁定成功
     */
    private Boolean locked;

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }
}
