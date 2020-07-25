package gulimall.common.to.es;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品在Es中保存的模型
 * @author 孙启新
 * <br>FileName: SkuEsModel
 * <br>Date: 2020/07/25 14:51:54
 */
@Data
public class SkuEsModel {
    /**
     * skuId
     */
    private Long skuId;
    /**
     * spuId
     */
    private Long spuId;
    /**
     * sku标题
     */
    private String skuTitle;
    /**
     * 价格
     */
    private BigDecimal skuPrice;
    /**
     * 默认图片
     */
    private String skuImg;
    /**
     * 销量
     */
    private Long saleCount;
    /**
     * 库存是否为空
     */
    private Boolean hasStock;
    /**
     * 热点评分
     */
    private Long hotScore;
    /**
     * 品牌id
     */
    private Long brandId;
    /**
     * 分类id
     */
    private Long catalogId;
    /**
     * 品牌名称
     */
    private String brandName;
    /**
     * 品牌图片
     */
    private String brandImg;
    /**
     * 分类名称
     */
    private  String catalogName;

    private List<attrs> attrs;

    @NoArgsConstructor
    @Data
    public static class attrs{
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
        private String attrValue;
    }
}
