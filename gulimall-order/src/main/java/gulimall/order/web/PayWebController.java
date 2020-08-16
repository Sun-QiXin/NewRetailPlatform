package gulimall.order.web;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import gulimall.order.config.AlipayConfig;
import gulimall.order.service.OrderService;
import gulimall.order.vo.PayAsyncVo;
import gulimall.order.vo.PayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 孙启新
 * <br>FileName: PayWebController
 * <br>Date: 2020/08/14 14:31:39
 */
@Controller
public class PayWebController {
    @Autowired
    private AlipayConfig alipayConfig;

    @Autowired
    private OrderService orderService;

    /**
     * 点击支付宝支付按钮跳转支付宝支付
     *
     * @param orderSn 订单号
     * @return 支付宝支付页
     * @throws AlipayApiException AlipayApiException
     */
    @ResponseBody
    @GetMapping(value = "/payOrder/{orderSn}", produces = "text/html")
    public String payOrder(@PathVariable("orderSn") String orderSn, HttpSession session) throws AlipayApiException {
        PayVo payVo = orderService.getOrderPayInfo(orderSn);
        if (payVo == null) {
            session.setAttribute("orderOvertimeMsg", "订单已超时,请重新下单！");
            return "<html>\n" +
                    "<head>\n" +
                    " <meta http-equiv=\"refresh\" content=\"0;url=http://order.gulimall.com/toTrade\">" +
                    "</head>\n" +
                    "<body>\n" +
                    "</body>\n" +
                    "</html>";
        }
        //调用该方法返回的是一个html页面，将该页面交给浏览器渲染即可
        return alipayConfig.pay(payVo);
    }

    /**
     * 只能让该session显示一次，显示完后，页面发送异步请求进行删除
     *
     * @return string
     */
    @GetMapping("/deleteOrderOvertimeMsg")
    @ResponseBody
    public String deleteOrderOvertimeMsg(HttpSession session) {
        session.removeAttribute("orderOvertimeMsg");
        return "ok";
    }

    /**
     * 支付成功后，支付宝会根据我们配置的异步通知地址给我们发送支付成功的通知，包含订单的所有信息
     * <br>默认只有支付成功才会通知(可修改，具体参照文档)
     *
     * @param payAsyncVo 支付成功的信息
     * @return 成功or失败
     */
    @PostMapping("/paymentNotifications")
    @ResponseBody
    public String paymentNotifications(PayAsyncVo payAsyncVo, HttpServletRequest request) throws AlipayApiException, ParseException {
        //1、首先进行验签再进行修改操作
        //从request中获取支付宝传来的所有请求参数
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        //2、调用SDK验证签名
        boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayConfig.getAlipay_public_key(), alipayConfig.getCharset(), alipayConfig.getSign_type());

        if (signVerified) {
            //说明是支付宝传来的，没有被篡改
            System.out.println("签名验证成功！");
            Boolean flag = orderService.handlePayResult(payAsyncVo);
            if (flag) {
                return "success";
            }
        }
        //只要不返回success，支付宝就会持续通知
        return "error";
    }
}
