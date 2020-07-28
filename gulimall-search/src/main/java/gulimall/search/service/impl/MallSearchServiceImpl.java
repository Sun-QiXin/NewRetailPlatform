package gulimall.search.service.impl;

import gulimall.search.service.MallSearchService;

import gulimall.search.vo.SearchParam;
import gulimall.search.vo.SearchResult;
import org.springframework.stereotype.Service;

/**
 * @author 孙启新
 * <br>FileName: MallSearchService
 * <br>Date: 2020/07/28 13:35:13
 */
@Service
public class MallSearchServiceImpl implements MallSearchService {
    /**
     * 根据传入的条件进行检索
     *
     * @param searchParam 检索参数
     * @return 返回检索结果
     */
    @Override
    public SearchResult search(SearchParam searchParam) {
        return null;
    }
}
