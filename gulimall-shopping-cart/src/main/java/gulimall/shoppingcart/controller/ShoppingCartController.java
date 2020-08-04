package gulimall.shoppingcart.controller;

import gulimall.common.constant.AuthServerConstant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

/**
 * @author 孙启新
 * <br>FileName: ShoppingCartController
 * <br>Date: 2020/08/04 15:08:15
 */
@Controller
public class ShoppingCartController {
    @GetMapping("/cartList.html")
    public String loginPage1(HttpSession session) {
        return "cartList";
    }
    @GetMapping("/success.html")
    public String loginPage2(HttpSession session) {
        return "success";
    }

}
