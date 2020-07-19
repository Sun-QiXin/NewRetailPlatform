package gulimall.product.vo;

import lombok.Data;

/**
 * 接收数据的Vo
 * @author 孙启新
 * <br>FileName: AttrGroupRelationVo
 * <br>Date: 2020/07/19 14:01:11
 */
@Data
public class AttrGroupRelationVo {
    /**
     * 属性id
     */
    private Long attrId;
    /**
     * 属性分组id
     */
    private Long attrGroupId;
}
