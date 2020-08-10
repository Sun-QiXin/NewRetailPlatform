package gulimall.member.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import gulimall.member.entity.MemberReceiveAddressEntity;
import gulimall.member.service.MemberReceiveAddressService;
import gulimall.common.utils.PageUtils;
import gulimall.common.utils.R;


/**
 * 会员收货地址
 *
 * @author SunQiXin
 * @email 15153869872@163.com
 * @date 2020-07-13 10:29:48
 */
@RestController
@RequestMapping("member/memberreceiveaddress")
public class MemberReceiveAddressController {
    @Autowired
    private MemberReceiveAddressService memberReceiveAddressService;

    /**
     * 根据会员id查询他的收货地址
     *
     * @param memberId 会员id
     * @return List<MemberReceiveAddressEntity>
     */
    @GetMapping("/getAddresses/{memberId}")
    public List<MemberReceiveAddressEntity> getAddresses(@PathVariable("memberId") Long memberId) {
        return memberReceiveAddressService.getAddresses(memberId);
    }

    /**
     * 更改当前的默认地址为新指定的
     *
     * @param memberId     用户id
     * @param defaultStatus 要更改成的信息
     * @return R对象
     */
    @GetMapping("/updateAddress")
    public R updateAddress(@RequestParam("memberId") Long memberId, @RequestParam("defaultStatus") Integer defaultStatus, @RequestParam("addressId") Long addressId) {
        memberReceiveAddressService.updateAddress(memberId, defaultStatus, addressId);
        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("member:memberreceiveaddress:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = memberReceiveAddressService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 根据ID获取收货地址信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        MemberReceiveAddressEntity memberReceiveAddress = memberReceiveAddressService.getById(id);
        return R.ok().put("memberReceiveAddress", memberReceiveAddress);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("member:memberreceiveaddress:save")
    public R save(@RequestBody MemberReceiveAddressEntity memberReceiveAddress) {
        memberReceiveAddressService.save(memberReceiveAddress);

        return R.ok();
    }

    /**
     * 修改地址信息
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberReceiveAddressEntity memberReceiveAddress) {
        memberReceiveAddressService.updateById(memberReceiveAddress);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("member:memberreceiveaddress:delete")
    public R delete(@RequestBody Long[] ids) {
        memberReceiveAddressService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
