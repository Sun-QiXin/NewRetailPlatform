package gulimall.product.vo;

import gulimall.product.entity.SkuImagesEntity;
import gulimall.product.entity.SkuInfoEntity;
import gulimall.product.entity.SpuInfoDescEntity;


import java.util.List;

/**
 * 返回商品详情页面的Vo
 *
 * @author 孙启新
 * <br>FileName: SkuItemVo
 * <br>Date: 2020/07/31 13:24:16
 */
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
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
     * spu的销售属性组合
     */
    private List<SkuItemSaleAttrsVo> saleAttrs;
    /**
     * spu的介绍
     */
    private SpuInfoDescEntity desp;
    /**
     * spu的分组规格参数信息。
     */
    private List<SpuItemBaseGroupAttrsVo> groupAttrs;
    /**
     * 当前商品的秒杀活动信息
     */
    private List<SeckillSkuVo> seckillSkuVos;

    public SkuInfoEntity getInfo() {
        return info;
    }

    public void setInfo(SkuInfoEntity info) {
        this.info = info;
    }

    public List<SkuImagesEntity> getImages() {
        return images;
    }

    public void setImages(List<SkuImagesEntity> images) {
        this.images = images;
    }

    public Boolean getHasStock() {
        return hasStock;
    }

    public void setHasStock(Boolean hasStock) {
        this.hasStock = hasStock;
    }

    public List<SkuItemSaleAttrsVo> getSaleAttrs() {
        return saleAttrs;
    }

    public void setSaleAttrs(List<SkuItemSaleAttrsVo> saleAttrs) {
        this.saleAttrs = saleAttrs;
    }

    public SpuInfoDescEntity getDesp() {
        return desp;
    }

    public void setDesp(SpuInfoDescEntity desp) {
        this.desp = desp;
    }

    public List<SpuItemBaseGroupAttrsVo> getGroupAttrs() {
        return groupAttrs;
    }

    public void setGroupAttrs(List<SpuItemBaseGroupAttrsVo> groupAttrs) {
        this.groupAttrs = groupAttrs;
    }

    public List<SeckillSkuVo> getSeckillSkuVos() {
        return seckillSkuVos;
    }

    public void setSeckillSkuVos(List<SeckillSkuVo> seckillSkuVos) {
        this.seckillSkuVos = seckillSkuVos;
    }
}
