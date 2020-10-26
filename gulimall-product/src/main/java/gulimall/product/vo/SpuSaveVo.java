package gulimall.product.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;


/**
 * 使用GsonFormat红菊根据前端传来的json自动生成的vo
 * @author 孙启新
 * <br>FileName: SpuSaveVo
 * <br>Date: 2020/07/21 10:49:30
 */

@NoArgsConstructor
@Data
public class SpuSaveVo {

    private String spuName;
    private String spuDescription;
    private Long catalogId;
    private Long brandId;
    private BigDecimal weight;
    private int publishStatus;
    private BoundsVo bounds;
    private List<String> decript;
    private List<String> images;
    private List<BaseAttrsVo> baseAttrs;
    private List<SkusVo> skus;

    @NoArgsConstructor
    @Data
    public static class BoundsVo {
        private BigDecimal buyBounds;
        private BigDecimal growBounds;
    }

    @NoArgsConstructor
    @Data
    public static class BaseAttrsVo {
        private Long attrId;
        private String attrValues;
        private int showDesc;
    }

    @NoArgsConstructor
    @Data
    public static class SkusVo {
        private String skuName;
        private BigDecimal price;
        private String skuTitle;
        private String skuSubtitle;
        private int fullCount;
        private BigDecimal discount;
        private int countStatus;
        private BigDecimal fullPrice;
        private BigDecimal reducePrice;
        private int priceStatus;
        private List<AttrVo> attr;
        private List<ImagesVo> images;
        private List<String> descar;
        private List<MemberPriceVo> memberPrice;

        @NoArgsConstructor
        @Data
        public static class AttrVo {
            private Long attrId;
            private String attrName;
            private String attrValue;
        }

        @NoArgsConstructor
        @Data
        public static class ImagesVo {
            private String imgUrl;
            private int defaultImg;
        }

        @NoArgsConstructor
        @Data
        public static class MemberPriceVo {
            private Long id;
            private String name;
            private BigDecimal price;
        }
    }
}
