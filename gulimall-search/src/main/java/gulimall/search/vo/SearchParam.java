package gulimall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * 封装页面所有可能传递的检索条件
 *
 * @author 孙启新
 * <br>FileName: SearchParam
 * <br>Date: 2020/07/28 13:32:25
 */
@Data
public class SearchParam {
    /**
     * 全文匹配关键字
     */
    private String keyword;
    /**
     * 3级分类id
     */
    private Long catalog3Id;
    /**
     * 结果排序
     * sort=saLeCount_asc/desc
     * sort=skuPrice_asc/desc
     * sort=hotScore_asc/desc
     */
    private String sort;
    /**
     * 过滤条件是否有货
     * hasStock(是否有货)、sluPrice区间、brandId、 catalog3Id、attrs
     * hasStock=e/1
     * skuPrice=0_500/_500/500_
     * brandId=111
     */
    private Integer hasStock;
    /**
     * 价格区间
     */
    private String skuPrice;
    /**
     * 品牌id,支持多选
     */
    private List<Long> brandId;
    /**
     * 按照属性进行筛选
     */
    private List<String> attrs;
    /**
     * 页码
     */
    private Integer pageNum = 1;
    /**
     * 请求参数
     */
    private String queryString;
}
