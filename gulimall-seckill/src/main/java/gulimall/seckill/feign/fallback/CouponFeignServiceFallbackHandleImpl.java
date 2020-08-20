package gulimall.seckill.feign.fallback;

import gulimall.common.exception.BizCodeEnume;
import gulimall.common.utils.R;
import gulimall.seckill.feign.CouponFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 远程调用失败的降级处理方法
 *
 * @author 孙启新
 * <br>FileName: CouponFeignServiceFallbackHandleImpl
 * <br>Date: 2020/08/20 16:35:25
 */
@Component
@Slf4j
public class CouponFeignServiceFallbackHandleImpl implements CouponFeignService {

    /**
     * 获取最近三天内的秒杀活动以及每个活动需要上架的商品
     *
     * @return R
     */
    @Override
    public R getLatestThreeDaysSessions() {
        log.error("--------------------------调用远程服务方法 getLatestThreeDaysSessions 失败,返回降级信息-------------------------");
        return R.error(BizCodeEnume.SENTINEL_DEGRADE_EXCEPTION.getCode(), BizCodeEnume.SENTINEL_DEGRADE_EXCEPTION.getMsg());
    }
}
