package gulimall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import gulimall.common.to.SkuHasStockVo;
import gulimall.common.utils.R;
import gulimall.product.entity.SkuImagesEntity;
import gulimall.product.entity.SpuInfoDescEntity;
import gulimall.product.feign.SeckillFeignService;
import gulimall.product.feign.WareFeignService;
import gulimall.product.service.*;

import gulimall.product.vo.SeckillSkuVo;
import gulimall.product.vo.SkuItemSaleAttrsVo;
import gulimall.product.vo.SkuItemVo;
import gulimall.product.vo.SpuItemBaseGroupAttrsVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import gulimall.common.utils.PageUtils;
import gulimall.common.utils.Query;

import gulimall.product.dao.SkuInfoDao;
import gulimall.product.entity.SkuInfoEntity;


/**
 * @author x3626
 */
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

    @Autowired
    private SeckillFeignService seckillFeignService;

    @Autowired
    private ThreadPoolExecutor executor;

    /**
     * sku的基本信息；pms_sku_info
     *
     * @param skuInfoEntity sku的基本信息
     */
    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.save(skuInfoEntity);
    }

    /**
     * 根据传来的参数进行查询
     *
     * @param params 传来的参数
     * @return 分页信息
     */
    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();
        //封装模糊查询的参数
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and(w ->
                    w.eq("sku_id", key).or().like("sku_name", key)
            );
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
     * @param spuId spuId
     * @return 所有sku信息，品牌的名字
     */
    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {
        return this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
    }

    /**
     * 根据skuId返回页面需要的商品数据
     * <br>使用异步方式执行
     *
     * @param skuId skuId
     * @return 商品数据
     */
    @Override
    public SkuItemVo itemSkuInfo(Long skuId) throws ExecutionException, InterruptedException {
        SkuItemVo skuItemVo = new SkuItemVo();
        /*使用异步编排的方式执行下面代码,由于下面代2、3、4步码的执行需要用到第一步的返回值，所以使用supplyAsync*/
        CompletableFuture<SkuInfoEntity> skuInfoFuture = CompletableFuture.supplyAsync(() -> {
            //1、sku基本信息获取   pms_sku_info
            SkuInfoEntity skuInfoEntity = this.getById(skuId);
            skuItemVo.setInfo(skuInfoEntity);
            return skuInfoEntity;
        }, executor);

        /*不需要返回值给其他人使用并且需要在第一个任务完成后调用所以用thenAcceptAsync*/
        CompletableFuture<Void> saleAttrFuture = skuInfoFuture.thenAcceptAsync(res -> {
            //2、获取spu的销售属性组合。
            List<SkuItemSaleAttrsVo> saleAttrsVos = skuSaleAttrValueService.getSaleAttrsBySpuId(res.getSpuId());
            skuItemVo.setSaleAttrs(saleAttrsVos);
        }, executor);

        /*不需要返回值给其他人使用并且需要在第一个任务完成后调用所以用thenAcceptAsync*/
        CompletableFuture<Void> infoDescFuture = skuInfoFuture.thenAcceptAsync(res -> {
            //3、获取spu的介绍 pms_spu_info_desc
            SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(res.getSpuId());
            skuItemVo.setDesp(spuInfoDescEntity);
        }, executor);

        /*不需要返回值给其他人使用并且需要在第一个任务完成后调用所以用thenAcceptAsync*/
        CompletableFuture<Void> groupAttrFuture = skuInfoFuture.thenAcceptAsync(res -> {
            //4、获取spu的规格参数信息。
            List<SpuItemBaseGroupAttrsVo> groupAttrsVos = attrGroupService.getAttrGroupWithAttrsBySpuId(res.getCatalogId(), res.getSpuId());
            skuItemVo.setGroupAttrs(groupAttrsVos);
        }, executor);

        /*使用异步编排的方式执行下面代码,由于下面代码不需要该步返回值，所以使用runAsync*/
        CompletableFuture<Void> imgFuture = CompletableFuture.runAsync(() -> {
            //5、sku的图片信息    pms_sku_images
            List<SkuImagesEntity> skuImagesEntities = skuImagesService.getImagesById(skuId);
            skuItemVo.setImages(skuImagesEntities);
        }, executor);

        /*使用异步编排的方式执行下面代码,由于下面代码不需要该步返回值，所以使用runAsync*/
        CompletableFuture<Void> wareFuture = CompletableFuture.runAsync(() -> {
            //6、远程查询当前商品是否有库存
            R r = wareFeignService.getSkuHasStock(Collections.singletonList(skuId));
            if (r.getCode() == 0) {
                List<SkuHasStockVo> hasStockVos = r.getData(new TypeReference<List<SkuHasStockVo>>() {
                });
                for (SkuHasStockVo hasStockVo : hasStockVos) {
                    skuItemVo.setHasStock(hasStockVo.getHasStock());
                }
            }
        }, executor);

        /*使用异步编排的方式执行下面代码,由于下面代码不需要该步返回值，所以使用runAsync*/
        CompletableFuture<Void> seckillFuture = CompletableFuture.runAsync(() -> {
            //7、远程查询当前商品是否有秒杀活动
            R r = seckillFeignService.getSkuSeckillInfoById(skuId);
            if (r.getCode() == 0) {
                List<SeckillSkuVo> seckillSkuVos = r.getData(new TypeReference<List<SeckillSkuVo>>() {
                });
                if (seckillSkuVos != null && seckillSkuVos.size() > 0) {
                    //将里面对象降序排序
                    seckillSkuVos.sort(Comparator.comparing(SeckillSkuVo::getStartTime));
                    skuItemVo.setSeckillSkuVos(seckillSkuVos);
                }
            }
        }, executor);

        /*等所有异步任务都完成再返回*/
        CompletableFuture.allOf(skuInfoFuture, saleAttrFuture, infoDescFuture, groupAttrFuture, imgFuture, wareFuture, seckillFuture).get();
        return skuItemVo;
    }

    /**
     * 根据skuId查询商品价格
     *
     * @param skuId 商品skuId
     * @return r对象
     */
    @Override
    public BigDecimal currentPrice(Long skuId) {
        SkuInfoEntity skuInfoEntity = this.getById(skuId);
        return skuInfoEntity.getPrice();
    }
}