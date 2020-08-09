package gulimall.member.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import gulimall.common.utils.PageUtils;
import gulimall.common.utils.Query;

import gulimall.member.dao.MemberReceiveAddressDao;
import gulimall.member.entity.MemberReceiveAddressEntity;
import gulimall.member.service.MemberReceiveAddressService;


@Service("memberReceiveAddressService")
public class MemberReceiveAddressServiceImpl extends ServiceImpl<MemberReceiveAddressDao, MemberReceiveAddressEntity> implements MemberReceiveAddressService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberReceiveAddressEntity> page = this.page(
                new Query<MemberReceiveAddressEntity>().getPage(params),
                new QueryWrapper<MemberReceiveAddressEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 根据会员id查询他的收货地址
     *
     * @param memberId 会员id
     * @return List<MemberReceiveAddressEntity>
     */
    @Override
    public List<MemberReceiveAddressEntity> getAddresses(Long memberId) {
        return this.list(new QueryWrapper<MemberReceiveAddressEntity>().eq("member_id", memberId));
    }

    /**
     * 更改当前的默认地址为新指定的
     * @param memberId     用户id
     * @param defaultStatus 要更改成的信息
     * @param addressId 要更改成默认地址的列id
     */
    @Override
    public void updateAddress(Long memberId, Integer defaultStatus, Long addressId) {
        //1、首先将之前的默认地址状态置为0
        List<MemberReceiveAddressEntity> memberReceiveAddressEntities = this.list(new QueryWrapper<MemberReceiveAddressEntity>().eq("member_id", memberId).eq("default_status", 1));
        if (memberReceiveAddressEntities != null && memberReceiveAddressEntities.size() > 0) {
            for (MemberReceiveAddressEntity memberReceiveAddressEntity : memberReceiveAddressEntities) {
                memberReceiveAddressEntity.setId(memberReceiveAddressEntity.getId());
                memberReceiveAddressEntity.setDefaultStatus(0);
                this.updateById(memberReceiveAddressEntity);
            }
        }

        //2、当前传递的地址为默认地址
        MemberReceiveAddressEntity memberReceiveAddress = new MemberReceiveAddressEntity();
        memberReceiveAddress.setId(addressId);
        memberReceiveAddress.setMemberId(memberId);
        memberReceiveAddress.setDefaultStatus(defaultStatus);
        this.updateById(memberReceiveAddress);
    }
}