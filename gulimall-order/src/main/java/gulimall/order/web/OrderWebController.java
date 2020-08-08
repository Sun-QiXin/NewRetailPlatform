package gulimall.order.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author 孙启新
 * <br>FileName: OrderWebController
 * <br>Date: 2020/08/08 15:30:23
 */
@Controller
public class OrderWebController {
    @GetMapping("/toTrade")
    public String toTrade() {
        return "confirm";
    }
}
