package gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 属性
 * @author x3626
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpuBaseAttrVo {
    /**
     * 属性值
     */
    private String attrValue;
    /**
     * 属性名
     */
    private String attrName;
}