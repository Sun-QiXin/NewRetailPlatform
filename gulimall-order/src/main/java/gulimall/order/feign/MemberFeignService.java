package gulimall.order.feign;


import gulimall.common.utils.R;
import gulimall.common.vo.MemberRespVo;
import gulimall.order.vo.MemberAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author 孙启新
 * <br>FileName: MemberFeignService
 * <br>Date: 2020/08/09 10:38:17
 */
@Component
@FeignClient("gulimall-member")
public interface MemberFeignService {

    /**
     * 根据会员id查询他的收货地址
     *
     * @param memberId 会员id
     * @return List<MemberReceiveAddressEntity>
     */
    @GetMapping("/member/memberreceiveaddress/getAddresses/{memberId}")
    List<MemberAddressVo> getAddresses(@PathVariable("memberId") Long memberId);

    /**
     * 更改当前的默认地址为新指定的
     *
     * @param memberId     id
     * @param defaultStatus 要更改成的信息
     * @param addressId 要更改成默认地址的列id
     * @return R对象
     */
    @RequestMapping("/member/memberreceiveaddress/updateAddress")
    R updateAddress(@RequestParam("memberId") Long memberId, @RequestParam("defaultStatus") Integer defaultStatus,@RequestParam("addressId") Long addressId);

    /**
     * 根据ID获取收货地址信息
     * @param id 地址id
     * @return R 对象
     */
    @RequestMapping("/member/memberreceiveaddress/info/{id}")
    R getAddressById(@PathVariable("id") Long id);

    /**
     * 根据用户id修改用户信息
     * @param memberRespVo 用户信息
     * @return R
     */
    @RequestMapping("/member/member/update")
    R updateById(@RequestBody MemberRespVo memberRespVo);

    /**
     * 根据用户id获取用户信息信息
     * @param id 用户id
     * @return R
     */
    @RequestMapping("/member/member/info/{id}")
    R getInfoById(@PathVariable("id") Long id);
}
