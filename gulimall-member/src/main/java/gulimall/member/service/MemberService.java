package gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import gulimall.common.utils.PageUtils;
import gulimall.member.entity.MemberEntity;
import gulimall.member.exception.EmailExistException;
import gulimall.member.exception.PhoneExistException;
import gulimall.member.exception.UsernameExistException;
import gulimall.member.vo.MemberLoginVo;
import gulimall.member.vo.MemberRegisterVo;

import java.util.Map;

/**
 * 会员
 *
 * @author SunQiXin
 * @email 15153869872@163.com
 * @date 2020-07-13 10:29:48
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 注册用户
     *
     * @param memberRegisterVo 用户信息
     */
    void register(MemberRegisterVo memberRegisterVo);

    /**
     * 检查邮箱是否唯一
     *
     * @param email 邮箱
     * @throws EmailExistException 邮箱不存在异常
     */
    void checkEmailUnique(String email) throws EmailExistException;

    /**
     * 检查手机号是否唯一
     *
     * @param phone 手机号
     * @throws PhoneExistException 手机号不存在异常
     */
    void checkPhoneUnique(String phone) throws PhoneExistException;

    /**
     * 检查用户名是否唯一
     *
     * @param username 用户名
     * @throws UsernameExistException 用户名不存在异常
     */
    void checkUsernameUnique(String username) throws UsernameExistException;

    /**
     * 登录用户
     * @param memberLoginVo 登录信息
     * @return 用户对象
     */
    MemberEntity login(MemberLoginVo memberLoginVo);
}

