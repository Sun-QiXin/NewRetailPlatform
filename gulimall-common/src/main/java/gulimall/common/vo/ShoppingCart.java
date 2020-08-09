package gulimall.common.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车
 * 要计算的属性，必须重写他的get方法，保证每次获取属性都会进行计算
 *
 * @author 孙启新
 * <br>FileName: ShoppingCart
 * <br>Date: 2020/08/05 11:02:01
 */
public class ShoppingCart implements Serializable {
    /**
     * 购物项集合
     */
    private List<ShoppingCartItem> items;
    /**
     * 购物车中商品的总数量
     */
    private Integer countNum;
    /**
     * 购物车中有几种商品
     */
    private Integer countType;
    /**
     * 当前选中商品总价
     */
    private BigDecimal totalPrice;
    /**
     * 减免价格
     */
    private BigDecimal reduce = new BigDecimal(0);

    public List<ShoppingCartItem> getItems() {
        return items;
    }

    public void setItems(List<ShoppingCartItem> items) {
        this.items = items;
    }

    public Integer getCountNum() {
        this.countNum = 0;
        if (items != null && items.size() > 0) {
            for (ShoppingCartItem item : items) {
                this.countNum += item.getCount();
            }
        }
        return countNum;
    }

    public Integer getCountType() {
        this.countType = 0;
        if (items != null) {
            this.countType = items.size();
        }
        return countType;
    }

    public BigDecimal getTotalPrice() {
        this.totalPrice = new BigDecimal(0);
        //1、计算购物项总价
        if (items != null && items.size() > 0) {
            for (ShoppingCartItem item : items) {
                if (item.getCheck()){
                    this.totalPrice = this.totalPrice.add(item.getSubtotalPrice());
                }
            }
        }
        //2、减去优惠总价
        this.totalPrice = this.totalPrice.subtract(this.getReduce());
        return totalPrice;
    }

    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }
}
