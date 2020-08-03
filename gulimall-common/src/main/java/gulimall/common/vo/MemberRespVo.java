package gulimall.common.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 孙启新
 * <br>FileName: MemberRespVo
 * <br>Date: 2020/08/03 14:00:28
 */
@Data
public class MemberRespVo implements Serializable {
    private Long id;
    /**
     * 会员等级id
     */
    private Long levelId;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 手机号码
     */
    private String mobile;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 头像
     */
    private String header;
    /**
     * 性别
     */
    private Integer gender;
    /**
     * 生日
     */
    private Date birth;
    /**
     * 所在城市
     */
    private String city;
    /**
     * 职业
     */
    private String job;
    /**
     * 个性签名
     */
    private String sign;
    /**
     * 用户来源
     */
    private Integer sourceType;
    /**
     * 积分
     */
    private Integer integration;
    /**
     * 成长值
     */
    private Integer growth;
    /**
     * 启用状态
     */
    private Integer status;
    /**
     * 注册时间
     */
    private Date createTime;
    /**
     *社交登录用户唯一id
     */
    private String socialUid;
    /**
     *社交登录的访问令牌
     */
    private String accessToken;
    /**
     *社交登录返回的令牌过期时间
     */
    private String expiresIn;
}
