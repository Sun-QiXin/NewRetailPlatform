package gulimall.shoppingcart.controller;

import gulimall.common.constant.AuthServerConstant;
import gulimall.shoppingcart.interceptor.ShoppingCartInterceptor;
import gulimall.shoppingcart.service.ShoppingCartService;
import gulimall.shoppingcart.to.UserInfoTo;
import gulimall.shoppingcart.vo.ShoppingCartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.concurrent.ExecutionException;

/**
 * @author 孙启新
 * <br>FileName: ShoppingCartController
 * <br>Date: 2020/08/04 15:08:15
 */
@Controller
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 点击链接跳转购物车页面，并获取购物车数据
     *
     * @param session session对象
     * @return 购物车页面
     */
    @GetMapping("/cartList.html")
    public String cartListPage(HttpSession session) {
        //1、快速得到拦截器中保存的用户信息，使用ThreadLocal（每一个线程都绑定了一个userInfoTo）
        UserInfoTo userInfoTo = ShoppingCartInterceptor.threadLocal.get();
        System.out.println(userInfoTo);
        return "cartList";
    }

    /**
     * 加入购物车
     *
     * @param skuId 商品的skuId
     * @param num   商品数量
     * @param model model
     * @return 成功页面
     */
    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num, Model model) {
        ShoppingCartItem shoppingCartItem = null;
        try {
            shoppingCartItem = shoppingCartService.addToCart(skuId, num);
        } catch (Exception e) {
            e.printStackTrace();
        }
        model.addAttribute("shoppingCartItem", shoppingCartItem);
        return "success";
    }
}
