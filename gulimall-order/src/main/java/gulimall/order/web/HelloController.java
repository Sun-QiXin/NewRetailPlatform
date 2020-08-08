package gulimall.order.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author 孙启新
 * <br>FileName: HelloController
 * <br>Date: 2020/08/08 13:09:42
 */
@Controller
public class HelloController {
    @GetMapping("{page}.html")
    public String showPage(@PathVariable("page") String page) {
        return page;
    }
}
