package gulimall.thirdparty;

import com.aliyun.oss.OSSClient;
import gulimall.thirdparty.component.SmsComponent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Map;

@SpringBootTest
class GulimallThirdPartyApplicationTests {
    @Autowired
    private SmsComponent smsComponent;

    @Resource
    private OSSClient ossClient;

    /**
     * 测试使用阿里云的对象oss文件上传
     * 使用阿里云的对象存储步骤
     * 1、引入oss-starter
     * 2、在yml文件配置key, endpoint相关信息即可
     * 3、使osSClient进行相关操作
     *
     * @throws FileNotFoundException
     */
    @Test
    public void testUpload() throws FileNotFoundException {
        // 上传文件流。
        InputStream inputStream = new FileInputStream("E:\\图片\\壁纸\\ChMlWV5cfoGIADyDABB0Ie_Y2VUAANhjQNgkf0AEHQ5884.jpg");
        ossClient.putObject("sunqixin-gulimal", "test2.jpg", inputStream);

        // 关闭OSSClient。
        ossClient.shutdown();
        System.out.println("上传成功！");
    }


    /**
     * 测试发送短信验证码
     */
    @Test
    public void sendSms() {
        String host = "https://feginesms.market.alicloudapi.com";// 【1】请求地址 支持http 和 https 及 WEBSOCKET
        String path = "/codeNotice";// 【2】后缀
        String appcode = "a580c0902a654d0b8a29b2faf519e2c2"; // 【3】开通服务后 买家中心-查看AppCode
        String sign = "1"; // 【4】请求参数，详见文档描述
        String skin = "13"; // 【4】请求参数，详见文档描述
        String param = "111111"; // 【4】请求参数，详见文档描述
        String phone = "15153869872"; // 【4】请求参数，详见文档描述
        String urlSend = host + path + "?sign=" + sign + "&skin=" + skin + "&param=" + param + "&phone=" + phone; // 【5】拼接请求链接
        try {
            URL url = new URL(urlSend);
            HttpURLConnection httpURLCon = (HttpURLConnection) url.openConnection();
            httpURLCon.setRequestProperty("Authorization", "APPCODE " + appcode);// 格式Authorization:APPCODE
            // (中间是英文空格)
            int httpCode = httpURLCon.getResponseCode();
            if (httpCode == 200) {
                String json = read(httpURLCon.getInputStream());
                System.out.println("正常请求计费(其他均不计费)");
                System.out.println("获取返回的json:");
                System.out.print(json);
            } else {
                Map<String, List<String>> map = httpURLCon.getHeaderFields();
                String error = map.get("X-Ca-Error-Message").get(0);
                if (httpCode == 400 && error.equals("Invalid AppCode `not exists`")) {
                    System.out.println("AppCode错误 ");
                } else if (httpCode == 400 && error.equals("Invalid Url")) {
                    System.out.println("请求的 Method、Path 或者环境错误");
                } else if (httpCode == 400 && error.equals("Invalid Param Location")) {
                    System.out.println("参数错误");
                } else if (httpCode == 403 && error.equals("Unauthorized")) {
                    System.out.println("服务未被授权（或URL和Path不正确）");
                } else if (httpCode == 403 && error.equals("Quota Exhausted")) {
                    System.out.println("套餐包次数用完 ");
                } else {
                    System.out.println("参数名错误 或 其他错误");
                    System.out.println(error);
                }
            }

        } catch (MalformedURLException e) {
            System.out.println("URL格式错误");
        } catch (UnknownHostException e) {
            System.out.println("URL地址错误");
        } catch (Exception e) {
            // 打开注释查看详细报错异常信息
            // e.printStackTrace();
        }

    }

    /**
     * 测试封装后的发送短信验证码方法
     */
    @Test
    public void sendSms2(){
        String msg = smsComponent.sendSmsCode("15153869872", "666666");
        System.out.println(msg);
    }

    /*
     * 读取返回结果
     */
    private static String read(InputStream is) throws IOException {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        while ((line = br.readLine()) != null) {
            line = new String(line.getBytes(), StandardCharsets.UTF_8);
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }
}
