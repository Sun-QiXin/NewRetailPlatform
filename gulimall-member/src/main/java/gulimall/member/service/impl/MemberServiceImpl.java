package gulimall.member.service.impl;

import gulimall.member.entity.MemberLevelEntity;
import gulimall.member.exception.EmailExistException;
import gulimall.member.exception.PhoneExistException;
import gulimall.member.exception.UsernameExistException;
import gulimall.member.service.MemberLevelService;
import gulimall.member.vo.MemberLoginVo;
import gulimall.member.vo.MemberRegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import gulimall.common.utils.PageUtils;
import gulimall.common.utils.Query;

import gulimall.member.dao.MemberDao;
import gulimall.member.entity.MemberEntity;
import gulimall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    private MemberLevelService memberLevelService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 注册用户
     *
     * @param memberRegisterVo 用户信息
     */
    @Override
    public void register(MemberRegisterVo memberRegisterVo) {
        MemberEntity memberEntity = new MemberEntity();
        //设置默认等级
        MemberLevelEntity levelEntity = memberLevelService.getOne(new QueryWrapper<MemberLevelEntity>().eq("default_status", 1));
        memberEntity.setLevelId(levelEntity.getId());

        //检查用户名、手机号是否唯一不唯一则抛出异常
        checkUsernameUnique(memberRegisterVo.getUsername());
        checkPhoneUnique(memberRegisterVo.getUsername());

        //设置用户名
        memberEntity.setUsername(memberRegisterVo.getUsername());

        //设置密码，使用spring提供BCryptPasswordEncoder的密码编码器加密存储
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String password = passwordEncoder.encode(memberRegisterVo.getPassword());
        memberEntity.setPassword(password);

        //设置手机号
        memberEntity.setMobile(memberRegisterVo.getPhone());
        this.save(memberEntity);
    }

    /**
     * 检查邮箱是否唯一
     *
     * @param email 邮箱
     * @throws EmailExistException 邮箱不存在异常
     */
    @Override
    public void checkEmailUnique(String email) throws EmailExistException {
        int count = this.count(new QueryWrapper<MemberEntity>().eq("email", email));
        if (count > 0) {
            throw new EmailExistException();
        }
    }

    /**
     * 检查手机号是否唯一
     *
     * @param phone 手机号
     * @throws PhoneExistException 手机号不存在异常
     */
    @Override
    public void checkPhoneUnique(String phone) throws PhoneExistException {
        int count = this.count(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if (count > 0) {
            throw new PhoneExistException();
        }
    }

    /**
     * 检查用户名是否唯一
     *
     * @param username 用户名
     * @throws UsernameExistException 用户名不存在异常
     */
    @Override
    public void checkUsernameUnique(String username) throws UsernameExistException {
        int count = this.count(new QueryWrapper<MemberEntity>().eq("username", username));
        if (count > 0) {
            throw new UsernameExistException();
        }
    }

    /**
     * 登录用户
     *
     * @param memberLoginVo 登录信息
     * @return 用户对象
     */
    @Override
    public MemberEntity login(MemberLoginVo memberLoginVo) {
        String password = memberLoginVo.getPassword();
        String username = memberLoginVo.getUsername();
        //1、查询当前用户名的加密密码取出对比
        MemberEntity memberEntity = this.getOne(new QueryWrapper<MemberEntity>().eq("username", username).or().eq("mobile", username));
        if (memberEntity == null) {
            //登录失败
            return null;
        } else {
            //数据库保存的密码与原密码匹配
            String dbPassword = memberEntity.getPassword();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            //匹配成功返回true(即登录成功)
            boolean flag = passwordEncoder.matches(password, dbPassword);
            if (flag) {
                return memberEntity;
            } else {
                return null;
            }
        }
    }
}