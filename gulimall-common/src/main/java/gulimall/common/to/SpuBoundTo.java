package gulimall.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 孙启新
 * <br>FileName: SpuBoundTo
 * <br>Date: 2020/07/21 12:48:29
 */
@Data
public class SpuBoundTo {
    private Long spuId;

    private BigDecimal buyBounds;

    private BigDecimal growBounds;
}
