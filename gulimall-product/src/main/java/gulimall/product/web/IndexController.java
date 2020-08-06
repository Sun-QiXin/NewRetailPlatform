package gulimall.product.web;

import gulimall.product.entity.CategoryEntity;
import gulimall.product.service.CategoryService;
import gulimall.product.vo.catagory2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;


/**
 * @author 孙启新
 * <br>FileName: IndexController
 * <br>Date: 2020/07/26 09:42:51
 */
@Controller
public class IndexController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 跳转首页
     * @return
     */
    @GetMapping({"/","/index","/index.html"})
    public String indexPage(Model model){
        //查出所有的1级分类
        List<CategoryEntity> categoryEntities = categoryService.getLeve1Categorys();
        model.addAttribute("categorys", categoryEntities);
        return "index";
    }

    /**
     * 获取2级3级分类的json
     * @return
     */
    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<catagory2Vo>> getCatalogJson(){
        return categoryService.getCatalogJson();
    }
}
