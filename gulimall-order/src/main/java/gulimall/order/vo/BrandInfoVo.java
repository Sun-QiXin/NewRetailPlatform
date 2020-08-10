package gulimall.order.vo;

import lombok.Data;

/**
 * @author 孙启新
 * <br>FileName: BrandInfoVO
 * <br>Date: 2020/08/10 14:55:12
 */
@Data
public class BrandInfoVo {

    /**
     * 品牌id
     */
    private Long brandId;
    /**
     * 品牌名
     */
    private String name;
    /**
     * 品牌logo地址
     */
    private String logo;
    /**
     * 介绍
     */
    private String descript;
    /**
     * 显示状态[0-不显示；1-显示]
     */
    private Integer showStatus;
    /**
     * 检索首字母
     */
    private String firstLetter;
    /**
     * 排序
     */
    private Integer sort;
}
