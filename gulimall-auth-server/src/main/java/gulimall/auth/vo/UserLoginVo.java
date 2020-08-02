package gulimall.auth.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

/**
 * 登录用的vo
 * @author 孙启新
 * <br>FileName: UserLoginVo
 * <br>Date: 2020/08/02 14:54:31
 */
@Data
public class UserLoginVo {
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
}
