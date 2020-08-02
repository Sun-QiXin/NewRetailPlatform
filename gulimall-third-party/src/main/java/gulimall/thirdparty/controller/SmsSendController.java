package gulimall.thirdparty.controller;

import gulimall.common.utils.R;
import gulimall.thirdparty.component.SmsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短信
 *
 * @author 孙启新
 * <br>FileName: SmsSendController
 * <br>Date: 2020/08/02 09:13:05
 */
@RestController
@RequestMapping("/sms")
public class SmsSendController {
    @Autowired
    private SmsComponent smsComponent;

    /**
     * 发送验证码
     *
     * @param phone 手机号
     * @param code  验证码
     * @return R对象
     */
    @GetMapping("/sendCode")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code) {
        String msg = smsComponent.sendSmsCode(phone, code);
        return R.ok(msg);
    }
}
