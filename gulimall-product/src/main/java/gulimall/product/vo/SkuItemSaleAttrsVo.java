package gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * spu的销售属性
 *
 * @author x3626
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkuItemSaleAttrsVo {
    /**
     * 属性id
     */
    private Long attrId;
    /**
     * 属性名
     */
    private String attrName;
    /**
     * 属性值
     */
    private String attrValues;
}