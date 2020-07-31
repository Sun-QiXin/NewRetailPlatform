package gulimall.product.vo;

import gulimall.product.entity.SkuImagesEntity;
import gulimall.product.entity.SkuInfoEntity;
import gulimall.product.entity.SpuInfoDescEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 返回商品详情页面的Vo
 *
 * @author 孙启新
 * <br>FileName: SkuItemVo
 * <br>Date: 2020/07/31 13:24:16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkuItemVo {
    /**
     * sku基本信息获取   pms_sku_info
     */
    private SkuInfoEntity info;
    /**
     * sku的图片信息    pms_sku_images
     */
    private List<SkuImagesEntity> images;
    /**
     * 是否有货
     */
    private Boolean hasStock = true;
    /**
     * 获取spu的销售属性组合
     */
    private List<SkuItemSaleAttrsVo> saleAttrs;
    /**
     * 获取spu的介绍
     */
    private SpuInfoDescEntity desp;
    /**
     * 获取spu的分组规格参数信息。
     */
    private List<SpuItemBaseGroupAttrsVo> groupAttrs;
}
