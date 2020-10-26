package gulimall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import gulimall.order.vo.PayVo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 支付宝支付的相关配置
 *
 * @author x3626
 */
@ConfigurationProperties(prefix = "alipay")
@Component
@Data
@Slf4j
public class AlipayConfig {
    /**
     * 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
     */
    public String app_id;

    /**
     * 商户私钥，您的PKCS8格式RSA2私钥
     */
    public String merchant_private_key;

    /**
     * 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
     */
    public String alipay_public_key;

    /**
     * <br>服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
     * <br>支付宝会异步的给我们发送一个请求，告诉我们支付成功的信息
     */
    private String notify_url;

    /**
     * <br>页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
     * <br>同步通知，支付成功，一般跳转到成功页
     */
    private String return_url;

    /**
     * 签名方式
     */
    private String sign_type = "RSA2";

    /**
     * 字符编码格式
     */
    private String charset = "utf-8";

    /**
     * 支付宝网关； https://openapi.alipaydev.com/gateway.do
     */
    private String gatewayUrl;
    /**
     * 支付宝付款自动收单时间(超时后该订单不可支付)
     */
    private String timeout_express = "30m";

    /**
     * 响应支付宝的收银台页面
     *
     * @param vo 传入的订单信息
     * @return 收银台页面
     * @throws AlipayApiException AlipayApiException
     */
    public String pay(PayVo vo) throws AlipayApiException {
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填(属性名不可修改)
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        //构建请求参数
        alipayRequest.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\","
                + "\"total_amount\":\"" + total_amount + "\","
                + "\"subject\":\"" + subject + "\","
                + "\"body\":\"" + body + "\","
                + "\"timeout_express\":\"" + timeout_express + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应：" + result);

        return result;
    }

    /**
     * 收单交易关闭方法,两个参数必须传一个
     *
     * @param trade_no     交易流水号
     * @param out_trade_no 商家订单号
     */
    public void alipayTradeClose(String trade_no, String out_trade_no) throws AlipayApiException {
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个收单请求
        AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();

        //3、设置请求参数
        if (StringUtils.isEmpty(trade_no)) {
            request.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\"}");
        } else {
            request.setBizContent("{\"trade_no\":\"" + trade_no + "\"}");
        }

        String result = alipayClient.execute(request).getBody();
        log.info(result);
    }
}
