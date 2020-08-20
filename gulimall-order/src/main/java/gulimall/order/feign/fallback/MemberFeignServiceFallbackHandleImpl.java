package gulimall.order.feign.fallback;


import gulimall.common.exception.BizCodeEnume;
import gulimall.common.utils.R;
import gulimall.common.vo.MemberRespVo;
import gulimall.order.feign.MemberFeignService;
import gulimall.order.vo.MemberAddressVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 远程调用失败的降级处理方法
 *
 * @author 孙启新
 * <br>FileName: MemberFeignServiceFallbackHandleImpl
 * <br>Date: 2020/08/20 16:35:25
 */
@Component
@Slf4j
public class MemberFeignServiceFallbackHandleImpl implements MemberFeignService {
    /**
     * 根据会员id查询他的收货地址
     *
     * @param memberId 会员id
     * @return List<MemberReceiveAddressEntity>
     */
    @Override
    public List<MemberAddressVo> getAddresses(Long memberId) {
        log.error("--------------------------调用远程服务方法 getAddresses 失败,返回降级信息-------------------------");
        return null;
    }

    /**
     * 更改当前的默认地址为新指定的
     *
     * @param memberId      id
     * @param defaultStatus 要更改成的信息
     * @param addressId     要更改成默认地址的列id
     * @return R对象
     */
    @Override
    public R updateAddress(Long memberId, Integer defaultStatus, Long addressId) {
        log.error("--------------------------调用远程服务方法 updateAddress 失败,返回降级信息-------------------------");
        return R.error(BizCodeEnume.SENTINEL_DEGRADE_EXCEPTION.getCode(), BizCodeEnume.SENTINEL_DEGRADE_EXCEPTION.getMsg());
    }

    /**
     * 根据ID获取收货地址信息
     *
     * @param id 地址id
     * @return R 对象
     */
    @Override
    public R getAddressById(Long id) {
        log.error("--------------------------调用远程服务方法 getAddressById 失败,返回降级信息-------------------------");
        return R.error(BizCodeEnume.SENTINEL_DEGRADE_EXCEPTION.getCode(), BizCodeEnume.SENTINEL_DEGRADE_EXCEPTION.getMsg());
    }

    /**
     * 根据用户id修改用户信息
     *
     * @param memberRespVo 用户信息
     * @return R
     */
    @Override
    public R updateById(MemberRespVo memberRespVo) {
        log.error("--------------------------调用远程服务方法 updateById 失败,返回降级信息-------------------------");
        return R.error(BizCodeEnume.SENTINEL_DEGRADE_EXCEPTION.getCode(), BizCodeEnume.SENTINEL_DEGRADE_EXCEPTION.getMsg());
    }

    /**
     * 根据用户id获取用户信息信息
     *
     * @param id 用户id
     * @return R
     */
    @Override
    public R getInfoById(Long id) {
        log.error("--------------------------调用远程服务方法 getInfoById 失败,返回降级信息-------------------------");
        return R.error(BizCodeEnume.SENTINEL_DEGRADE_EXCEPTION.getCode(), BizCodeEnume.SENTINEL_DEGRADE_EXCEPTION.getMsg());
    }
}
