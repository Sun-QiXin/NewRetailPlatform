package gulimall.product.feign;

import gulimall.common.to.SkuReductionTo;
import gulimall.common.to.SpuBoundTo;
import gulimall.common.utils.R;
import gulimall.product.feign.fallback.CouponFeignServiceFallbackHandleImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author 孙启新
 * <br>FileName: OrderFeignService
 * <br>Date: 2020/07/13 12:02:08
 */
@Component
@FeignClient(value = "gulimall-coupon",fallback = CouponFeignServiceFallbackHandleImpl.class)
public interface CouponFeignService {
    /**
     * <br>保存spu的积分信息；gulimall_sms->sms_spu_bounds
     * <br>1、CouponFeignService.saveSpuBounds(spuBoundTo);
     *      <br>1）、@RequestBody将这个对象转为json。
     *      <br>2）、找到gulimall-coupon服务，给/coupon/spubounds/save发送请求。
     *          <br>将上一步转的json放在请求体位置，发送请求；
     *      <br>3）、对方服务收到请求。请求体里有json数据。
     *          <br>(@RequestBody SpuBoundsEntity spuBounds)；将请求体的json转为SpuBoundsEntity；
     * <br>只要json数据模型是兼容的。双方服务无需使用同一个to
     * @param spuBoundTo
     * @return
     */
    @RequestMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);

    /**
     * sku的优惠、满减等信息；gulimall_sms->sms_sku_ladder
     * @param skuReductionTo
     */
    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
