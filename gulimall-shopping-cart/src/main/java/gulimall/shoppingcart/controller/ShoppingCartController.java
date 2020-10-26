package gulimall.shoppingcart.controller;


import gulimall.shoppingcart.service.ShoppingCartService;

import gulimall.common.vo.ShoppingCart;
import gulimall.common.vo.ShoppingCartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;


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
     * 用于保存加入购物车时返回的商品信息用于成功页展示
     */
    public static ShoppingCartItem shoppingCartItem;

    /**
     * 点击链接跳转购物车页面，并获取购物车数据
     *
     * @param model model
     * @return 购物车页面
     */
    @GetMapping("/cartList.html")
    public String cartListPage(Model model, HttpSession session) throws ExecutionException, InterruptedException {
        ShoppingCart shoppingCart = shoppingCartService.getCartList();
        session.setAttribute("shoppingCartInfo", shoppingCart);
        model.addAttribute("shoppingCart", shoppingCart);
        return "cartList";
    }

    /**
     * 加入购物车（存入redis）
     *
     * @param skuId 商品的skuId
     * @param num   商品数量
     * @return 成功页面
     */
    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num) throws ExecutionException, InterruptedException {
        shoppingCartItem = shoppingCartService.addToCart(skuId, num);
        //转发成功页面,这么可以防止刷新页面就添加购物车
        return "redirect:http://cart.gulimall.com/addToCartSuccess.html";
    }

    /**
     * 删除购物车中的商品
     *
     * @param skuIds 商品的skuIds集合
     * @return 购物车页面
     */
    @PostMapping("/clearCartProduct")
    public String addToCart(@RequestParam("skuIds") String skuIds) {
        shoppingCartService.clearCartProduct(skuIds);
        return "redirect:http://cart.gulimall.com/cartList.html";
    }

    /**
     * 修改购物车商品的选中状态
     *
     * @param skuId skuId
     * @param check 当前选中状态
     * @return 购物车页面
     */
    @GetMapping("/checkItem")
    public String checkItem(@RequestParam("skuId") Long skuId, @RequestParam("check") Integer check) {
        shoppingCartService.checkItem(skuId, check);
        return "redirect:http://cart.gulimall.com/cartList.html";
    }

    /**
     * 修改购物车商品的件数
     *
     * @param skuId skuId
     * @param count 要修改成的件数
     * @return 购物车页面
     */
    @GetMapping("/countItem")
    public String countItem(@RequestParam("skuId") Long skuId, @RequestParam("count") Integer count) {
        shoppingCartService.countItem(skuId, count);
        return "redirect:http://cart.gulimall.com/cartList.html";
    }

    /**
     * 获取当前登录用户的购物车数据返回
     * @return ShoppingCart
     * @throws ExecutionException ExecutionException
     * @throws InterruptedException InterruptedException
     */
    @GetMapping("/currentUserShoppingCart")
    @ResponseBody
    public ShoppingCart getCurrentUserShoppingCart() throws ExecutionException, InterruptedException {
        return shoppingCartService.getCurrentUserShoppingCart();
    }

    /**
     * 跳转加入购物车成功页面
     *
     * @param model model
     * @return success页面
     */
    @GetMapping("/addToCartSuccess.html")
    public String addToCartSuccessPage(Model model) {
        model.addAttribute("shoppingCartItem", shoppingCartItem);
        return "success";
    }
}
