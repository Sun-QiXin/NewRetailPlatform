package gulimall.ware.feign.fallback;

import gulimall.common.exception.BizCodeEnume;
import gulimall.common.utils.R;
import gulimall.ware.feign.OrderFeignService;
import gulimall.ware.feign.ProductFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 远程调用失败的降级处理方法
 *
 * @author 孙启新
 * <br>FileName: OrderFeignServiceFallbackHandleImpl
 * <br>Date: 2020/08/20 16:35:25
 */
@Component
@Slf4j
public class OrderFeignServiceFallbackHandleImpl implements OrderFeignService {

    /**
     * 根据订单号获取订单的详细信息
     *
     * @param orderSn 订单号
     * @return 订单的详细信息
     */
    @Override
    public R getOrder(String orderSn) {
        log.error("--------------------------调用远程服务方法 getOrder 失败,返回降级信息-------------------------");
        return R.error(BizCodeEnume.SENTINEL_DEGRADE_EXCEPTION.getCode(), BizCodeEnume.SENTINEL_DEGRADE_EXCEPTION.getMsg());
    }
}
