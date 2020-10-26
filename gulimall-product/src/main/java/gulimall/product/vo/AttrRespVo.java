package gulimall.product.vo;


import lombok.Data;

/**
 * 响应数据的vo
 * @author 孙启新
 * <br>FileName: AttrRespVo
 * <br>Date: 2020/07/18 15:14:43
 */
@Data
public class AttrRespVo extends AttrVo {
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
    private  Long[] catelogPath;
}
