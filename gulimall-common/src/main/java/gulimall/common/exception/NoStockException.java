package gulimall.common.exception;

/**
 * @author 孙启新
 * <br>FileName: NoStockException
 * <br>Date: 2020/08/10 16:49:55
 */
public class NoStockException extends RuntimeException {
    /**
     * 商品的ID
     */
    private Long skuId;

    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public NoStockException(Long skuId) {
        super("skuId为" + skuId + "的商品没有足够的库存了");
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }
}
