package gulimall.product.vo;

import gulimall.product.entity.AttrEntity;
import gulimall.product.entity.AttrGroupEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 分类下所有分组&关联属性
 *
 * @author 孙启新
 * <br>FileName: AttrGroupWithAttrsVo
 * <br>Date: 2020/07/21 09:44:53
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class AttrGroupWithAttrsVo extends AttrGroupEntity {
    /**
     * 属性列表
     */
    private List<AttrEntity> attrs;
}
