package gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import gulimall.common.to.SkuHasStockVo;
import gulimall.common.utils.PageUtils;
import gulimall.ware.entity.WareSkuEntity;
import gulimall.ware.vo.LockStockResultVo;
import gulimall.ware.vo.WareSkuLockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author SunQiXin
 * @email 15153869872@163.com
 * @date 2020-07-13 10:32:25
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    /**
     * 分页查询，带模糊条件查询
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 将成功采购的进行入库
     * @param skuId
     * @param wareId
     * @param skuNum
     */
    void addStock(Long skuId, Long wareId, Integer skuNum);

    /**
     * 查询是否有库存
     * @param skuIds
     * @return
     */
    List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds);

    /**
     * 根据传来的数据锁定某件商品的库存
     * @param wareSkuLockVo wareSkuLockVo
     * @return 是否锁定成功
     */
    Boolean orderLockStock(WareSkuLockVo wareSkuLockVo);
}

