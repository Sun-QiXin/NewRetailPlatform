package gulimall.search.controller;

import gulimall.search.service.MallSearchService;
import gulimall.search.vo.SearchParam;
import gulimall.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 孙启新
 * <br>FileName: SearchController
 * <br>Date: 2020/07/28 13:19:35
 */
@Controller
public class SearchController {
    @Autowired
    private MallSearchService mallSearchService;

    /**
     * 跳转到list页面，然后根据传来的检索参数查询出指定的数据
     * @return 检索后的数据
     */
    @GetMapping("/list.html")
    public String listPage(SearchParam searchParam, Model model, HttpServletRequest request){
        searchParam.setQueryString(request.getQueryString());
        SearchResult result = mallSearchService.search(searchParam);
        model.addAttribute("result",result);
        return "list";
    }
}
