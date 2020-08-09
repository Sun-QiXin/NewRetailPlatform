package gulimall.order.feign;


import gulimall.common.utils.R;
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

}
