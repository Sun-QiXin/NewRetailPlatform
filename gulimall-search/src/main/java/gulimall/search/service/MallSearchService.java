package gulimall.search.service;

import gulimall.search.vo.SearchParam;
import gulimall.search.vo.SearchResult;

/**
 * @author 孙启新
 * <br>FileName: MallSearchService
 * <br>Date: 2020/07/28 13:35:13
 */
public interface MallSearchService {
    /**
     * 根据传入的条件进行检索
     * @param searchParam   检索参数
     * @return 返回检索结果
     */
    SearchResult search(SearchParam searchParam);
}
