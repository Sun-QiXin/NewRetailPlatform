package gulimall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import gulimall.common.exception.BizCodeEnume;
import gulimall.common.to.SkuHasStockVo;
import gulimall.ware.vo.LockStockResultVo;
import gulimall.ware.vo.WareSkuLockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import gulimall.ware.entity.WareSkuEntity;
import gulimall.ware.service.WareSkuService;
import gulimall.common.utils.PageUtils;
import gulimall.common.utils.R;


/**
 * 商品库存
 *
 * @author SunQiXin
 * @email 15153869872@163.com
 * @date 2020-07-13 10:32:25
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 根据传来的数据锁定某件商品的库存
     * @param wareSkuLockVo wareSkuLockVo
     * @return R
     */
    @PostMapping("/lock/order")
    public R orderLockStock(@RequestBody WareSkuLockVo wareSkuLockVo){
        try {
            wareSkuService.orderLockStock(wareSkuLockVo);
            return R.ok();
        } catch (Exception e) {
            return R.error(BizCodeEnume.NO_STOCK_EXCEPTION.getCode(),e.getMessage());
        }
    }

    /**
     * 根据skuIds集合批量查询是否有库存
     *
     * @param skuIds skuId集合
     * @return R
     */
    @PostMapping("/hasStock")
    public R getSkuHasStock(@RequestBody List<Long> skuIds) {
        List<SkuHasStockVo> skuHasStockVos = wareSkuService.getSkuHasStock(skuIds);
        return R.ok().setData(skuHasStockVos);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("ware:waresku:info")
    public R info(@PathVariable("id") Long id) {
        WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("ware:waresku:save")
    public R save(@RequestBody WareSkuEntity wareSku) {
        wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("ware:waresku:update")
    public R update(@RequestBody WareSkuEntity wareSku) {
        wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:waresku:delete")
    public R delete(@RequestBody Long[] ids) {
        wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
