package gulimall.product.vo;

import lombok.Data;

/**
 * @author 孙启新
 * <br>FileName: AttrValueWithSkuIdVo
 * <br>Date: 2020/08/01 09:23:30
 */
@Data
public class AttrValueWithSkuIdVo {
    /**
     * 属性值
     */
    private String attrValue;
    /**
     * 一种颜色对应的sku集合
     */
    private String skuIds;
}
