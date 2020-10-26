package gulimall.product.vo;

import lombok.Data;

/**
 * 品牌响应数据的Vo
 * @author 孙启新
 * <br>FileName: BrandRespVo
 * <br>Date: 2020/07/21 09:21:18
 */
@Data
public class BrandRespVo {
    /**
     * 品牌的Id
     */
    private Long brandId;

    /**
     * 品牌的名字
     */
    private String brandName;
}
