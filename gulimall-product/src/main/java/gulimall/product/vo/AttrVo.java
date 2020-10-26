package gulimall.product.vo;

import gulimall.product.entity.AttrEntity;
import lombok.Data;

/**
 * 接收数据的vo
 * @author 孙启新
 * <br>FileName: AttrVo
 * <br>Date: 2020/07/18 14:39:05
 */
@Data
public class AttrVo extends AttrEntity {
    /**
     * 额外添加分组id字段
     */
    private Long attrGroupId;
}
