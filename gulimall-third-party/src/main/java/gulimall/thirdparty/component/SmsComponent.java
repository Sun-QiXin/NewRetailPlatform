package gulimall.thirdparty.component;

import gulimall.common.utils.HttpUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


/**
 * 发送短信的类
 *
 * @author 孙启新
 * <br>FileName: SmsComponent
 * <br>Date: 2020/08/01 14:59:38
 */
@Component
@Data
@ConfigurationProperties(prefix = "spring.cloud.alicloud.sms")
public class SmsComponent {
    /**
     * 短信服务，请求地址 支持http 和 https 及 WEBSOCKET
     */
    private String host;
    /**
     *  短信服务，后缀
     */
    private String path;
    /**
     *  短信服务，签名
     */
    private String sign;
    /**
     *  短信服务，模板内容
     */
    private String skin;
    /**
     *  短信服务，appcode
     */
    private String appcode;

    /**
     * 发送短信的方法
     *
     * @param phoneNumber 手机号
     * @param code        验证码
     */
    public String sendSmsCode(String phoneNumber, String code) {
        // 拼接请求链接
        String urlSend = host + path + "?sign=" + sign + "&skin=" + skin + "&param=" + code + "&phone=" + phoneNumber;
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        return HttpUtils.get(urlSend, headers);
    }
}
