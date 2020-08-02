package gulimall.auth.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @author 孙启新
 * <br>FileName: UserRegisterVo
 * <br>Date: 2020/08/02 10:20:54
 */
@Data
public class UserRegisterVo {
    /**
     * 用户名
     */
    @NotEmpty(message = "用户名必须提交")
    @Length(min = 6,max = 11,message = "用户名必须是6~11位字符")
    private String username;
    /**
     * 密码
     */
    @NotEmpty(message = "密码必须提交")
    @Length(min = 6,max = 11,message = "密码必须是6~11位字符")
    private String password;
    /**
     * 手机号
     */
    @NotEmpty(message = "手机号必须提交")
    @Pattern(regexp = "^[1]([3-9])[0-9]{9}$",message = "手机号格式不正确")
    private String phone;
    /**
     * 验证码
     */
    @NotEmpty(message = "验证码必须提交")
    private String code;
}
