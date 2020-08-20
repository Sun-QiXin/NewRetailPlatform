package gulimall.product.feign.fallback;

import gulimall.common.exception.BizCodeEnume;
import gulimall.common.to.SkuReductionTo;
import gulimall.common.to.SpuBoundTo;
import gulimall.common.utils.R;
import gulimall.product.feign.CouponFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * 远程调用失败的降级处理方法
 * @author 孙启新
 * <br>FileName: CouponFeignServiceFallbackHandle
 * <br>Date: 2020/08/20 16:35:25
 */
@Component
@Slf4j
public class CouponFeignServiceFallbackHandleImpl implements CouponFeignService {
    /**
     * <br>保存spu的积分信息；gulimall_sms->sms_spu_bounds
     * <br>1、CouponFeignService.saveSpuBounds(spuBoundTo);
     * <br>1）、@RequestBody将这个对象转为json。
     * <br>2）、找到gulimall-coupon服务，给/coupon/spubounds/save发送请求。
     * <br>将上一步转的json放在请求体位置，发送请求；
     * <br>3）、对方服务收到请求。请求体里有json数据。
     * <br>(@RequestBody SpuBoundsEntity spuBounds)；将请求体的json转为SpuBoundsEntity；
     * <br>只要json数据模型是兼容的。双方服务无需使用同一个to
     *
     * @param spuBoundTo
     * @return
     */
    @Override
    public R saveSpuBounds(SpuBoundTo spuBoundTo) {
        log.error("--------------------------调用远程服务方法 saveSpuBounds 失败,返回降级信息-------------------------");
        return R.error(BizCodeEnume.SENTINEL_DEGRADE_EXCEPTION.getCode(), BizCodeEnume.SENTINEL_DEGRADE_EXCEPTION.getMsg());
    }

    /**
     * sku的优惠、满减等信息；gulimall_sms->sms_sku_ladder
     *
     * @param skuReductionTo skuReductionTo
     */
    @Override
    public R saveSkuReduction(SkuReductionTo skuReductionTo) {
        log.error("--------------------------调用远程服务方法 saveSkuReduction 失败,返回降级信息-------------------------");
        return R.error(BizCodeEnume.SENTINEL_DEGRADE_EXCEPTION.getCode(), BizCodeEnume.SENTINEL_DEGRADE_EXCEPTION.getMsg());
    }
}
