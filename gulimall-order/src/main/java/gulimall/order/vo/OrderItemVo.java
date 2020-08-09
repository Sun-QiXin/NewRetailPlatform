package gulimall.order.vo;


import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 所有选中商品的vo
 *
 * @author 孙启新
 * <br>FileName: OrderItemVo
 * <br>Date: 2020/08/09 10:27:58
 */
@Data
public class OrderItemVo {
    /**
     * 商品id
     */
    private Long skuId;
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
    /**
     * 是否有货
     */
    private Boolean hasStock = true;
}
