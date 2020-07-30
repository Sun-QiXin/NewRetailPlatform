package gulimall.search.vo;

import gulimall.common.to.es.SkuEsModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.ArrayList;
import java.util.List;

/**
 * 返回页面的vo
 *
 * @author 孙启新
 * <br>FileName: SearchResponse
 * <br>Date: 2020/07/28 13:55:41
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult {
    /**
     * 查询到的所有商品信息
     */
    private List<SkuEsModel> products;
    /**
     * 当前页码
     */
    private Integer pageNum;
    /**
     * 总记录数
     */
    private Long total;
    /**
     * 总页数
     */
    private Integer totalPages;
    /**
     * 存储页码
     */
    private List<Integer> pageNavs;
    /**
     * 当前查询商品涉及到的品牌
     */
    private List<BrandVo> brands;
    /**
     * 当前查询商品涉及到的属性
     */
    private List<attrVo> attrs;
    /**
     * 当前查询商品涉及到的分类
     */
    private List<CatalogVo> catalogs;
    /**
     * 面包屑导航数据
     */
    private List<navVo> navs = new ArrayList<>();
    private List<Long> attrIds = new ArrayList<>();

    /**
     * 品牌信息VO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BrandVo {
        /**
         * 品牌id
         */
        private Long brandId;
        /**
         * 品牌名称
         */
        private String brandName;
        /**
         * 品牌图片
         */
        private String brandImg;
    }

    /**
     * 属性信息VO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class attrVo {
        /**
         * 属性id
         */
        private Long attrId;
        /**
         * 属性名称
         */
        private String attrName;
        /**
         * 属性值
         */
        private List<String> attrValue;
    }

    /**
     * 分类信息VO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CatalogVo {
        /**
         * 分类id
         */
        private Long catalogId;
        /**
         * 分类名称
         */
        private String catalogName;
    }

    /**
     * 面包屑导航数据信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class navVo {
        /**
         * 导航名字
         */
        private String navName;
        /**
         * 导航值
         */
        private String navValue;
        /**
         * 跳转地址
         */
        private String link;
    }
}
