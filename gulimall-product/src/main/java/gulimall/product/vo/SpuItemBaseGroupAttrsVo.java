package gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * spu的分组规格参数
 * @author x3626
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpuItemBaseGroupAttrsVo {
    /**
     * 分组名称
     */
    private String groupName;
    /**
     * 属性集合
     */
    private List<SpuBaseAttrVo> attrs;
}