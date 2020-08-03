package gulimall.member.vo;

import lombok.Data;

/**
 * 社交登录，换取token后返回的vo
 * @author 孙启新
 * <br>FileName: SocialUserVo
 * <br>Date: 2020/08/03 12:09:17
 */
@Data
public class SocialUserVo {
    /**
     * token，用户授权的唯一票据
     */
    private String access_token;
    /**
     * access_ _token的生命周期(该参数即将废弃，开发者清使用expires_ jin).
     */
    private String remind_in;
    /**
     * access_ token的生命周期， 单位是秒數。
     */
    private String expires_in;
    /**
     * 用户id
     */
    private String uid;
    /**
     * 是否实名
     */
    private String isRealName;
}
