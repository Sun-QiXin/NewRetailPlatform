package gulimall.thirdparty.component;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.List;
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
        // 【5】拼接请求链接
        String urlSend = host + path + "?sign=" + sign + "&skin=" + skin + "&param=" + code + "&phone=" + phoneNumber;
        try {
            URL url = new URL(urlSend);
            HttpURLConnection httpUrlCon = (HttpURLConnection) url.openConnection();
            // 格式Authorization:APPCODE
            // (中间是英文空格)
            httpUrlCon.setRequestProperty("Authorization", "APPCODE " + appcode);
            int httpCode = httpUrlCon.getResponseCode();
            if (httpCode == 200) {
                return read(httpUrlCon.getInputStream());
            } else {
                Map<String, List<String>> map = httpUrlCon.getHeaderFields();
                String error = map.get("X-Ca-Error-Message").get(0);
                if (httpCode == 400 && "Invalid AppCode `not exists`".equals(error)) {
                    return "AppCode错误 ";
                } else if (httpCode == 400 && "Invalid Url".equals(error)) {
                    return "请求的 Method、Path 或者环境错误";
                } else if (httpCode == 400 && "Invalid Param Location".equals(error)) {
                    return "参数错误";
                } else if (httpCode == 403 && "Unauthorized".equals(error)) {
                    return "服务未被授权（或URL和Path不正确）";
                } else if (httpCode == 403 && "Quota Exhausted".equals(error)) {
                    return "套餐包次数用完 ";
                } else {
                    return error;
                }
            }
        } catch (MalformedURLException e) {
            return "URL格式错误";
        } catch (UnknownHostException e) {
            return "URL地址错误";
        } catch (Exception e) {
            // 打开注释查看详细报错异常信息
            e.printStackTrace();
            return "出现异常：{}";
        }
    }

    /**
     * 读取返回结果
     * @param is 输入流
     * @return json
     * @throws IOException io异常
     */
    private static String read(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = br.readLine()) != null) {
            line = new String(line.getBytes(), StandardCharsets.UTF_8);
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }
}
