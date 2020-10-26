package gulimall.member.vo;

import lombok.Data;

/**
 * 登录用的vo
 * @author 孙启新
 * <br>FileName: UserLoginVo
 * <br>Date: 2020/08/02 14:54:31
 */
@Data
public class MemberLoginVo {
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
}
