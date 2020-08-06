package gulimall.common.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 购物项内容
 *
 * @author 孙启新
 * <br>FileName: ShoppingCart
 * <br>Date: 2020/08/05 11:02:01
 */
public class ShoppingCartItem implements Serializable {
    /**
     * 商品id
     */
    private Long skuId;
    /**
     * 商品是否被选中
     */
    private Boolean check = false;
    /**
     * 商品标题
     */
    private String title;
    /**
     * 商品默认图片
     */
    private String image;
    /**
     * 商品套餐
     */
    private List<String> skuAttr;
    /**
     * 商品价格
     */
    private BigDecimal price;
    /**
     * 当前商品件数
     */
    private Integer count;
    /**
     * 商品小计价格
     */
    private BigDecimal subtotalPrice;

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getSkuAttr() {
        return skuAttr;
    }

    public void setSkuAttr(List<String> skuAttr) {
        this.skuAttr = skuAttr;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    /**
     * 计算当前项总价
     *
     * @return 小计
     */
    public BigDecimal getSubtotalPrice() {
        this.subtotalPrice = this.price.multiply(new BigDecimal(this.count));
        return subtotalPrice;
    }

    public void setSubtotalPrice(BigDecimal subtotalPrice) {
        this.subtotalPrice = subtotalPrice;
    }
}
