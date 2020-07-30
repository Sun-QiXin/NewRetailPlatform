package gulimall.search.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author 孙启新
 * <br>FileName: AttrResponseVo
 * <br>Date: 2020/07/30 16:31:30
 */
@Data
public class AttrResponseVo {
    /**
     * 额外添加分组id字段
     */
    private Long attrGroupId;
    /**
     * 分类名称
     */
    private String catelogName;
    /**
     * 当前所属分组
     */
    private String groupName;
    /**
     * 完整菜单路径
     */
    private Long[] catelogPath;
    /**
     * 属性id
     */
    @TableId
    private Long attrId;
    /**
     * 属性名
     */
    private String attrName;
    /**
     * 是否需要检索[0-不需要，1-需要]
     */
    private Integer searchType;
    /**
     * 属性图标
     */
    private String icon;
    /**
     * 可选值列表[用逗号分隔]
     */
    private String valueSelect;
    /**
     * 属性类型[0-销售属性，1-基本属性，2-既是销售属性又是基本属性]
     */
    private Integer attrType;
    /**
     * 启用状态[0 - 禁用，1 - 启用]
     */
    private Long enable;
    /**
     * 所属分类
     */
    private Long catelogId;
    /**
     * 快速展示【是否展示在介绍上；0-否 1-是】，在sku中仍然可以调整
     */
    private Integer showDesc;
    /**
     * 值类型，多选单选
     */
    private Integer valueType;

}
