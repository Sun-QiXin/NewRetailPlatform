package gulimall.order.web;

import gulimall.common.exception.NoStockException;
import gulimall.common.utils.PageUtils;
import gulimall.order.service.OrderService;
import gulimall.order.vo.OrderConfirmVo;
import gulimall.order.vo.OrderSubmitVo;
import gulimall.order.vo.SubmitOrderResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.concurrent.ExecutionException;


/**
 * @author 孙启新
 * <br>FileName: OrderWebController
 * <br>Date: 2020/08/08 15:30:23
 */
@Controller
public class OrderWebController {
    @Autowired
    private OrderService orderService;

    /**
     * 跳转结算页，并展示当前需要展示的订单信息
     *
     * @param model model
     * @return orderConfirmVo
     * @throws ExecutionException   ExecutionException
     * @throws InterruptedException InterruptedException
     */
    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        OrderConfirmVo orderConfirmVo = orderService.confirmOrder();
        model.addAttribute("orderConfirmVo", orderConfirmVo);
        return "confirm";
    }

    /**
     * 提交订单
     *
     * @param orderSubmitVo orderSubmitVo
     * @param model         model
     * @return 支付页面
     */
    @RequestMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo orderSubmitVo, Model model, RedirectAttributes redirectAttributes) {
        try {
            SubmitOrderResponseVo submitOrderResponseVo = orderService.submitOrder(orderSubmitVo);
            if (submitOrderResponseVo.getCode() == 0) {
                //下单成功
                model.addAttribute("submitOrderResponseVo", submitOrderResponseVo);
                return "pay";
            } else {
                //返回结算页并显示失败原因
                String msg = "";
                if (submitOrderResponseVo.getCode() == 1) {
                    msg = "下单失败，请刷新后重新提交订单！";
                }
                redirectAttributes.addFlashAttribute("submitOrderErrorMsg", msg);
                return "redirect:http://order.gulimall.com/toTrade";
            }
        } catch (Exception e) {
            String msg = "下单的人太多了，请重新提交订单";
            if (e instanceof NoStockException) {
                msg = e.getMessage();
            }
            redirectAttributes.addFlashAttribute("submitOrderErrorMsg", msg);
            return "redirect:http://order.gulimall.com/toTrade";
        }
    }

    /**
     * 更改当前的默认地址为新指定的
     *
     * @param memberId      用户id
     * @param addressId     要更改成默认地址的列id
     * @param defaultStatus 要更改成的信息
     * @return 结算页
     */
    @GetMapping("/updateAddress")
    public String updateAddress(@RequestParam("memberId") Long memberId, @RequestParam("defaultStatus") Integer defaultStatus, @RequestParam("addressId") Long addressId) {
        orderService.updateAddress(memberId, defaultStatus, addressId);
        return "redirect:http://order.gulimall.com/toTrade";
    }

    /**
     * 支付成功后支付宝也会默认访问该请求
     * <br>分页查询出当前登录用户已经支付的订单信息
     *
     * @param model model
     * @param params 查询参数
     * @return 订单数据
     */
    @GetMapping("/list.html")
    public String orderListPage(@RequestParam Map<String, Object> params, Model model) {
        PageUtils page = orderService.queryPageWithItem(params);
        model.addAttribute("orders",page);
        return "list";
    }
}
