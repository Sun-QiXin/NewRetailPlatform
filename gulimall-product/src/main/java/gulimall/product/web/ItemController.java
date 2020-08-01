package gulimall.product.web;

import gulimall.product.service.SkuInfoService;
import gulimall.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;

/**
 * @author 孙启新
 * <br>FileName: ItemController
 * <br>Date: 2020/07/31 12:22:45
 */
@Controller
public class ItemController {
    @Autowired
    private SkuInfoService skuInfoService;

    /**
     * 根据skuId返回页面需要的商品数据
     *
     * @param model 模型对象
     * @param skuId skuId
     * @return 商品数据
     */
    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model) throws ExecutionException, InterruptedException {
        SkuItemVo skuItemVo = skuInfoService.itemSkuInfo(skuId);
        model.addAttribute("item", skuItemVo);
        return "item";
    }
}
