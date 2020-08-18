package gulimall.seckill.controller;

import gulimall.common.utils.R;
import gulimall.seckill.service.SeckillService;
import gulimall.seckill.to.SeckillSkuRedisTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 孙启新
 * <br>FileName: SeckillController
 * <br>Date: 2020/08/18 09:44:26
 */
@RestController
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    /**
     * 返回当前时间可以参与的秒杀商品
     * @return 秒杀商品
     */
    @GetMapping("/currentSeckillSkus")
    public R getCurrentSeckillSkus(){
        List<SeckillSkuRedisTo> skuRedisTos = seckillService.getCurrentSeckillSkus();
        return R.ok().setData(skuRedisTos);
    }

    /**
     * 根据skuId获取该商品是否有秒杀活动
     * @param skuId skuId
     * @return R
     */
    @GetMapping("/sku/seckill/{skuId}")
    public R getSkuSeckillInfoById(@PathVariable("skuId") Long skuId){
        List<SeckillSkuRedisTo> skuRedisTos = seckillService.getSkuSeckillInfoById(skuId);
        return R.ok().setData(skuRedisTos);
    }
}
