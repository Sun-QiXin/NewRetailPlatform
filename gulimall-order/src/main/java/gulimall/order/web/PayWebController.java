package gulimall.order.web;

import com.alipay.api.AlipayApiException;
import gulimall.order.config.AlipayConfig;
import gulimall.order.service.OrderService;
import gulimall.order.vo.PayVo;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

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
     * @return string
     */
    @GetMapping("/deleteOrderOvertimeMsg")
    @ResponseBody
    public String deleteOrderOvertimeMsg(HttpSession session){
        session.removeAttribute("orderOvertimeMsg");
        return "ok";
    }
}
