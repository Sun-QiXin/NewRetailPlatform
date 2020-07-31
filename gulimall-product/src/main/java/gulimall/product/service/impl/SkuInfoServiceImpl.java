package gulimall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import gulimall.common.to.SkuHasStockVo;
import gulimall.common.utils.R;
import gulimall.product.entity.SkuImagesEntity;
import gulimall.product.entity.SpuInfoDescEntity;
import gulimall.product.feign.WareFeignService;
import gulimall.product.service.*;

import gulimall.product.vo.SkuItemSaleAttrsVo;
import gulimall.product.vo.SkuItemVo;
import gulimall.product.vo.SpuItemBaseGroupAttrsVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import gulimall.common.utils.PageUtils;
import gulimall.common.utils.Query;

import gulimall.product.dao.SkuInfoDao;
import gulimall.product.entity.SkuInfoEntity;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {
    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private WareFeignService wareFeignService;

    /**
     * sku的基本信息；pms_sku_info
     *
     * @param skuInfoEntity
     */
    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.save(skuInfoEntity);
    }

    /**
     * 根据传来的参数进行查询
     *
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();
        //封装模糊查询的参数
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and(w -> {
                w.eq("sku_id", key).or().like("sku_name", key);
            });
        }
        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equals(catelogId)) {
            wrapper.eq("catalog_id", catelogId);
        }

        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equals(brandId)) {
            wrapper.eq("brand_id", brandId);
        }

        String min = (String) params.get("min");
        if (!StringUtils.isEmpty(min) && !"0".equals(min)) {
            wrapper.ge("price", min);
        }

        String max = (String) params.get("max");
        if (!StringUtils.isEmpty(max) && !"0".equals(max)) {
            wrapper.le("price", max);
        }

        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    /**
     * 查出当前spuId对应的所有sku信息，品牌的名字。
     *
     * @param spuId
     * @return
     */
    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {
        return this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
    }

    /**
     * 根据skuId返回页面需要的商品数据
     *
     * @param skuId skuId
     * @return 商品数据
     */
    @Override
    public SkuItemVo itemSkuInfo(Long skuId) {
        SkuItemVo skuItemVo = new SkuItemVo();
        //1、sku基本信息获取   pms_sku_info
        SkuInfoEntity skuInfoEntity = this.getById(skuId);
        Long catalogId = skuInfoEntity.getCatalogId();
        Long spuId = skuInfoEntity.getSpuId();
        skuItemVo.setInfo(skuInfoEntity);

        //2、sku的图片信息    pms_sku_images
        List<SkuImagesEntity> skuImagesEntities = skuImagesService.getImagesById(skuId);
        skuItemVo.setImages(skuImagesEntities);

        //3、获取spu的销售属性组合。
        List<SkuItemSaleAttrsVo> saleAttrsVos = skuSaleAttrValueService.getSaleAttrsBySpuId(spuId);
        skuItemVo.setSaleAttrs(saleAttrsVos);

        //4、获取spu的介绍 pms_spu_info_desc
        SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(spuId);
        skuItemVo.setDesp(spuInfoDescEntity);

        //5、获取spu的规格参数信息。
        List<SpuItemBaseGroupAttrsVo> groupAttrsVos = attrGroupService.getAttrGroupWithAttrsBySpuId(catalogId,spuId);
        skuItemVo.setGroupAttrs(groupAttrsVos);

        //6、远程查询当前商品是否有库存
        R r = wareFeignService.getSkuHasStock(Collections.singletonList(skuId));
        if (r.getCode()==0){
            List<SkuHasStockVo> hasStockVos = r.getData(new TypeReference<List<SkuHasStockVo>>() {
            });
            for (SkuHasStockVo hasStockVo : hasStockVos) {
                skuItemVo.setHasStock(hasStockVo.getHasStock());
            }
        }
        return skuItemVo;
    }
}