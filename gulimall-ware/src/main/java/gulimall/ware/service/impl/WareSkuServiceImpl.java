package gulimall.ware.service.impl;

import gulimall.common.to.SkuHasStockVo;
import gulimall.common.utils.R;
import gulimall.common.exception.NoStockException;
import gulimall.ware.feign.ProductFeignService;
import gulimall.ware.vo.OrderItemVo;
import gulimall.ware.vo.SkuWareHasStockVo;
import gulimall.ware.vo.WareSkuLockVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import gulimall.common.utils.PageUtils;
import gulimall.common.utils.Query;

import gulimall.ware.dao.WareSkuDao;
import gulimall.ware.entity.WareSkuEntity;
import gulimall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private ProductFeignService productFeignService;


    /**
     * 分页查询，带模糊条件查询
     *
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();

        //封装查询参数
        String skuId = (String) params.get("skuId");
        if (!StringUtils.isEmpty(skuId)) {
            wrapper.eq("sku_id", skuId);
        }
        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(wareId)) {
            wrapper.eq("ware_id", wareId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    /**
     * 将成功采购的进行入库
     *
     * @param skuId
     * @param wareId
     * @param skuNum
     */
    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //如果没有这个库存记录那就是新增操作
        List<WareSkuEntity> wareSkuEntities = this.list(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if (wareSkuEntities == null || wareSkuEntities.size() == 0) {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            //跨服务查询商品名字
            //TODO 还有什么办法让事务发生异常不回滚
            try {
                R info = productFeignService.info(skuId);
                if (info.getCode() == 0) {
                    Map<String, Object> skuInfo = (Map<String, Object>) info.get("skuInfo");
                    wareSkuEntity.setSkuName((String) skuInfo.get("skuName"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            baseMapper.insert(wareSkuEntity);
        } else {
            baseMapper.addStock(skuId, wareId, skuNum);
        }
    }

    /**
     * 查询是否有库存
     *
     * @param skuIds
     * @return
     */
    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
        return skuIds.stream().map(skuId -> {
            SkuHasStockVo hasStockVo = new SkuHasStockVo();
            //查询当前sku的库存量
            Long count = baseMapper.getSkuStock(skuId);
            hasStockVo.setSkuId(skuId);
            hasStockVo.setHasStock(count != null && count > 0);
            return hasStockVo;
        }).collect(Collectors.toList());
    }

    /**
     * 根据传来的数据锁定某件商品的库存
     *
     * @param wareSkuLockVo wareSkuLockVo
     * @return 是否锁定成功
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean orderLockStock(WareSkuLockVo wareSkuLockVo) {
        List<OrderItemVo> orderItemVos = wareSkuLockVo.getOrderItemVos();
        //1、找到每个商品在哪个仓库都有库存
        List<SkuWareHasStockVo> wareHasStockVos = orderItemVos.stream().map(item -> {
            Long skuId = item.getSkuId();
            SkuWareHasStockVo wareHasStockVo = new SkuWareHasStockVo();
            wareHasStockVo.setSkuId(skuId);
            wareHasStockVo.setNum(item.getCount());
            List<Long> wareIds = this.baseMapper.listWareIdHasSkuStock(skuId);
            wareHasStockVo.setWareIds(wareIds);
            return wareHasStockVo;
        }).collect(Collectors.toList());

        //2、锁定库存
        for (SkuWareHasStockVo wareHasStockVo : wareHasStockVos) {
            boolean skuStocked = false;
            Long skuId = wareHasStockVo.getSkuId();
            Integer num = wareHasStockVo.getNum();
            List<Long> wareIds = wareHasStockVo.getWareIds();
            if (wareIds == null || wareIds.size() == 0) {
                //没有仓库有该商品的库存
                throw new NoStockException(skuId);
            }
            for (Long wareId : wareIds) {
                //锁定库存
                int count = this.baseMapper.lockSkuStock(skuId, num, wareId);
                if (count == 1) {
                    //锁定成功
                    skuStocked = true;
                    break;
                }
                //当前仓库锁定失败，重试下一个仓库
            }
            if (!skuStocked) {
                //当前遍历的商品全部扣减库存失败
                throw new NoStockException(skuId);
            }
        }
        return true;
    }
}