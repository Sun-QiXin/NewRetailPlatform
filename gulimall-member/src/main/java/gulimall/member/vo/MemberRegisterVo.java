package gulimall.member.vo;

import lombok.Data;

/**
 * @author 孙启新
 * <br>FileName: MemberRegisterVo
 * <br>Date: 2020/08/02 12:10:08
 */
@Data
public class MemberRegisterVo {
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 手机号
     */
    private String phone;
}
