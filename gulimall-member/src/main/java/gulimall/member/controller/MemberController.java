package gulimall.member.controller;

import java.util.Arrays;
import java.util.Map;

import gulimall.common.exception.BizCodeEnume;
import gulimall.member.exception.PhoneExistException;
import gulimall.member.exception.UsernameExistException;
import gulimall.member.vo.MemberLoginVo;
import gulimall.member.vo.MemberRegisterVo;
import gulimall.member.vo.SocialUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import gulimall.member.entity.MemberEntity;
import gulimall.member.service.MemberService;
import gulimall.common.utils.PageUtils;
import gulimall.common.utils.R;

/**
 * 会员
 *
 * @author SunQiXin
 * @email 15153869872@163.com
 * @date 2020-07-13 10:29:48
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    /**
     * 注册用户
     *
     * @param memberRegisterVo 用户信息
     * @return R对象
     */
    @PostMapping("/register")
    public R register(@RequestBody MemberRegisterVo memberRegisterVo) {
        try {
            memberService.register(memberRegisterVo);
        } catch (PhoneExistException e) {
            return R.error(BizCodeEnume.PHONE_EXIST_EXCEPTION.getCode(), BizCodeEnume.PHONE_EXIST_EXCEPTION.getMsg());
        } catch (UsernameExistException e) {
            return R.error(BizCodeEnume.USERNAME_EXIST_EXCEPTION.getCode(), BizCodeEnume.USERNAME_EXIST_EXCEPTION.getMsg());
        }
        return R.ok();
    }

    /**
     * 登录用户
     * @param memberLoginVo 登录信息
     * @return R对象
     */
    @PostMapping("/login")
    public R login(@RequestBody MemberLoginVo memberLoginVo){
        MemberEntity memberEntity = memberService.login(memberLoginVo);
        if (memberEntity!=null){
            return R.ok().setData(memberEntity);
        }else {
            return R.error(BizCodeEnume.USERNAME_PASSWORD_ERROR_EXCEPTION.getCode(),BizCodeEnume.USERNAME_PASSWORD_ERROR_EXCEPTION.getMsg());
        }
    }


    /**
     * 社交登录
     * @param socialUserVo 登录信息
     * @return R对象
     */
    @PostMapping("oauth2/login")
    public R OAuth2login(@RequestBody SocialUserVo socialUserVo){
        MemberEntity memberEntity = memberService.OAuth2login(socialUserVo);
        if (memberEntity!=null){
            return R.ok().setData(memberEntity);
        }else {
            return R.error(BizCodeEnume.USERNAME_PASSWORD_ERROR_EXCEPTION.getCode(),BizCodeEnume.USERNAME_PASSWORD_ERROR_EXCEPTION.getMsg());
        }
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id) {
        MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member) {
        memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member) {
        memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids) {
        memberService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }

}
