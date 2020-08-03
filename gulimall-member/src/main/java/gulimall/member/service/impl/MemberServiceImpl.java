package gulimall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import gulimall.common.utils.HttpUtils;
import gulimall.member.entity.MemberLevelEntity;
import gulimall.member.exception.EmailExistException;
import gulimall.member.exception.PhoneExistException;
import gulimall.member.exception.UsernameExistException;
import gulimall.member.service.MemberLevelService;
import gulimall.member.vo.MemberLoginVo;
import gulimall.member.vo.MemberRegisterVo;
import gulimall.member.vo.SocialUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import gulimall.common.utils.PageUtils;
import gulimall.common.utils.Query;

import gulimall.member.dao.MemberDao;
import gulimall.member.entity.MemberEntity;
import gulimall.member.service.MemberService;
import org.springframework.util.StringUtils;


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

        //设置注册时间
        memberEntity.setCreateTime(new Date());

        //设置启用状态
        memberEntity.setStatus(1);
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

    /**
     * 社交登录
     *
     * @param socialUserVo 登录信息
     * @return MemberEntity
     */
    @Override
    public MemberEntity OAuth2login(SocialUserVo socialUserVo) {
        //登录和注册合并逻辑
        String uid = socialUserVo.getUid();
        String accessToken = socialUserVo.getAccess_token();
        String expiresIn = socialUserVo.getExpires_in();
        //1、判断当前社交用户是否是第一次登录系统;
        MemberEntity memberEntity = this.getOne(new QueryWrapper<MemberEntity>().eq("social_uid", uid));
        if (memberEntity != null) {
            //说明不是第一次登录
            memberEntity.setAccessToken(accessToken);
            memberEntity.setExpiresIn(expiresIn);
            this.updateById(memberEntity);
            return memberEntity;
        } else {
            //第一次登录，需要注册
            MemberEntity register = new MemberEntity();
            //3、 查询当前社交用户的社交账号信息(昵称，性别等)
            String userDetail = HttpUtils.get("https://api.weibo.com/2/users/show.json?access_token=" + accessToken + "&uid=" + uid, null);
            if (!StringUtils.isEmpty(userDetail)) {
                try {
                    JSONObject jsonObject = JSON.parseObject(userDetail);
                    String nickname = (String) jsonObject.get("name");
                    String location = (String) jsonObject.get("location");
                    String profileImageUrl = (String) jsonObject.get("profile_image_url");
                    String gender = (String) jsonObject.get("gender");
                    //设置昵称
                    register.setNickname(nickname);
                    //设置地址
                    register.setCity(location);
                    //设置性别
                    register.setGender("m".equals(gender) ? 1 : 0);
                    //设置头像
                    register.setHeader(profileImageUrl);
                    //设置默认等级
                    MemberLevelEntity levelEntity = memberLevelService.getOne(new QueryWrapper<MemberLevelEntity>().eq("default_status", 1));
                    register.setLevelId(levelEntity.getId());
                    //设置启用状态
                    register.setStatus(1);
                    //设置注册时间
                    register.setCreateTime(new Date());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //设置社交登录的访问令牌
                register.setAccessToken(accessToken);
                //设置社交登录返回的令牌过期时间
                register.setExpiresIn(expiresIn);
                //设置社交登录用户唯一id
                register.setSocialUid(uid);
                this.save(register);
                return register;
            }else {
                return null;
            }
        }
    }
}