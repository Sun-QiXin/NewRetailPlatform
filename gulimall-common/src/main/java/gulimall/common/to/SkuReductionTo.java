package gulimall.common.to;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 孙启新
 * <br>FileName: SkuReductionTo
 * <br>Date: 2020/07/21 13:18:38
 */
@Data
public class SkuReductionTo {
    private Long skuId;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPriceVo> memberPrice;

    @NoArgsConstructor
    @Data
    public static class MemberPriceVo {
        private Long id;
        private String name;
        private BigDecimal price;
    }
}
