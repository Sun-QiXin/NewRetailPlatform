package gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import gulimall.common.utils.PageUtils;
import gulimall.member.entity.MemberReceiveAddressEntity;

import java.util.List;
import java.util.Map;

/**
 * 会员收货地址
 *
 * @author SunQiXin
 * @email 15153869872@163.com
 * @date 2020-07-13 10:29:48
 */
public interface MemberReceiveAddressService extends IService<MemberReceiveAddressEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 根据会员id查询他的收货地址
     * @param memberId 会员id
     * @return List<MemberReceiveAddressEntity>
     */
    List<MemberReceiveAddressEntity> getAddresses(Long memberId);

    /**
     * 更改当前的默认地址为新指定的
     * @param memberId     用户id
     * @param defaultStatus 要更改成的信息
     * @param addressId 要更改成默认地址的列id
     */
    void updateAddress(Long memberId, Integer defaultStatus, Long addressId);
}

