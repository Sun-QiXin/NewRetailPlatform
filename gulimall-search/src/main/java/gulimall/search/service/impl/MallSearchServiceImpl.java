package gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import gulimall.common.to.es.SkuEsModel;
import gulimall.common.utils.R;
import gulimall.search.config.ElasticsearchConfig;
import gulimall.search.constant.EsConstant;
import gulimall.search.feign.ProductFeignService;
import gulimall.search.service.MallSearchService;

import gulimall.search.vo.AttrResponseVo;
import gulimall.search.vo.SearchParam;
import gulimall.search.vo.SearchResult;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 孙启新
 * <br>FileName: MallSearchService
 * <br>Date: 2020/07/28 13:35:13
 */
@Service
public class MallSearchServiceImpl implements MallSearchService {
    @Autowired
    @Qualifier("esRestClient")
    private RestHighLevelClient levelClient;

    @Autowired
    private ProductFeignService productFeignService;

    /**
     * 根据传入的条件进行检索
     *
     * @param searchParam 检索参数
     * @return 返回检索结果
     */
    @Override
    public SearchResult search(SearchParam searchParam) {
        //动态构建出查询需要得我DSL语句
        //1、准备检索请求
        SearchResult result = null;
        SearchRequest searchRequest = buildSearchRequest(searchParam);
        try {
            //2、执行检索请求
            SearchResponse searchResponse = levelClient.search(searchRequest, ElasticsearchConfig.COMMON_OPTIONS);

            //3、分析响应数据并封装为我们指定的格式
            result = buildSearchResult(searchResponse, searchParam);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 构建DSL语句,返回检索请求
     * 模糊匹配，过滤（按照属性，分类，品牌，价格区间，库存），排序，分页，高亮，聚合分析
     *
     * @param searchParam 检索参数
     * @return SearchRequest
     */
    private SearchRequest buildSearchRequest(SearchParam searchParam) {
        //构建DSL语句
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //1、must模糊匹配，过滤（按照属性，分类，品牌，价格区间，库存）
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //1.1、must 按照skuTitle查询
        if (!StringUtils.isEmpty(searchParam.getKeyword())) {
            boolQuery.must(QueryBuilders.matchQuery("skuTitle", searchParam.getKeyword()));
        }
        //1.2、filter 按照3级分类id查询
        if (searchParam.getCatalog3Id() != null) {
            boolQuery.filter(QueryBuilders.termQuery("catalogId", searchParam.getCatalog3Id()));
        }
        //1.3、filter 按照品牌id查询
        if (searchParam.getBrandId() != null && searchParam.getBrandId().size() > 0) {
            boolQuery.filter(QueryBuilders.termsQuery("brandId", searchParam.getBrandId()));
        }
        //1.4、filter 按照属性查询
        if (searchParam.getAttrs() != null && searchParam.getAttrs().size() > 0) {
            for (String attr : searchParam.getAttrs()) {
                BoolQueryBuilder nestedBoolQuery = QueryBuilders.boolQuery();
                String[] s = attr.split("_");
                //取出属性id
                String attrId = s[0];
                //检索的属性值
                String[] attrValues = s[1].split(":");

                nestedBoolQuery.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                nestedBoolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", attrValues));
                //每遍历一个必须都得生成一个nested查询
                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", nestedBoolQuery, ScoreMode.None);
                boolQuery.filter(nestedQuery);
            }
        }
        //1.5、filter 按照是否拥有库存进行查询
        if (searchParam.getHasStock() != null) {
            Boolean flag = searchParam.getHasStock() == 1;
            boolQuery.filter(QueryBuilders.termQuery("hasStock", flag));
        }
        //1.6、filter 按照价格区间进行查询 前端会传1_500\_500\500_
        if (!StringUtils.isEmpty(searchParam.getSkuPrice())) {
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
            String[] strings = searchParam.getSkuPrice().split("_");
            if (strings.length == 2) {
                //区间
                rangeQuery.gte(strings[0]).lte(strings[1]);
            } else if (strings.length == 1) {
                if (searchParam.getSkuPrice().startsWith("_")) {
                    rangeQuery.lte(strings[0]);
                } else if (searchParam.getSkuPrice().endsWith("_")) {
                    rangeQuery.gte(strings[0]);
                }
            }
            boolQuery.filter(rangeQuery);
        }
        //把上面所有的查询条件都放进去
        sourceBuilder.query(boolQuery);

        //2、排序，分页，高亮
        //2.1、排序
        if (!StringUtils.isEmpty(searchParam.getSort())) {
            String[] s = searchParam.getSort().split("_");
            SortOrder sortOrder = "asc".equalsIgnoreCase(s[1]) ? SortOrder.ASC : SortOrder.DESC;
            sourceBuilder.sort(s[0], sortOrder);
        }

        //2.2、分页
        //pageNum:l from:0 size:5 [0,1,2,3,4]
        //pageNum:2 from: 5 size:5
        //from = (pageNum-1)*size
        sourceBuilder.from((searchParam.getPageNum() - 1) * EsConstant.PRODUCT_PAGESIZE);
        sourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);

        //2.3、高亮
        if (!StringUtils.isEmpty(searchParam.getKeyword())) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            sourceBuilder.highlighter(highlightBuilder);
        }

        //3、聚合分析
        //3.1、品牌聚合
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
        brand_agg.field("brandId").size(50);
        //3.1.1、品牌聚合的子聚合
        brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName"));
        brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg"));
        sourceBuilder.aggregation(brand_agg);

        //3.2、分类聚合
        TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg");
        catalog_agg.field("catalogId").size(20);
        //3.2.1、分类聚合的子聚合
        catalog_agg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName"));
        sourceBuilder.aggregation(catalog_agg);

        //3.3、属性聚合
        NestedAggregationBuilder attr_agg = AggregationBuilders.nested("attr_agg", "attrs");
        //3.3.1、attr_agg子聚合
        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
        //3.3.2、attr_id_agg子聚合
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName"));
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue"));
        attr_agg.subAggregation(attr_id_agg);
        sourceBuilder.aggregation(attr_agg);

        return new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);
    }

    /**
     * 构建响应数据
     *
     * @param searchResponse,searchParam 搜索结果和请求参数
     * @return 搜索结果
     */
    private SearchResult buildSearchResult(SearchResponse searchResponse, SearchParam searchParam) {
        SearchResult searchResult = new SearchResult();
        SearchHits hits = searchResponse.getHits();
        //1、封装查询到的所有商品
        List<SkuEsModel> skuEsModels = new ArrayList<>();
        if (hits.getHits() != null && hits.getHits().length > 0) {
            for (SearchHit hit : hits.getHits()) {
                String sourceAsString = hit.getSourceAsString();
                SkuEsModel skuEsModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
                if (!StringUtils.isEmpty(searchParam.getKeyword())) {
                    HighlightField highlightField = hit.getHighlightFields().get("skuTitle");
                    String skuTitle = highlightField.getFragments()[0].string();
                    skuEsModel.setSkuTitle(skuTitle);
                }
                skuEsModels.add(skuEsModel);
            }
        }
        searchResult.setProducts(skuEsModels);

        //2、当前商品所涉及的所有属性信息
        List<SearchResult.attrVo> attrVos = new ArrayList<>();
        ParsedNested attr_agg = searchResponse.getAggregations().get("attr_agg");
        ParsedLongTerms attr_id_agg = attr_agg.getAggregations().get("attr_id_agg");
        for (Terms.Bucket bucket : attr_id_agg.getBuckets()) {
            SearchResult.attrVo attrVo = new SearchResult.attrVo();
            //得到属性的id
            Long attrId = bucket.getKeyAsNumber().longValue();
            attrVo.setAttrId(attrId);

            //得到属性的名字
            ParsedStringTerms attr_name_agg = bucket.getAggregations().get("attr_name_agg");
            String attrName = attr_name_agg.getBuckets().get(0).getKeyAsString();
            attrVo.setAttrName(attrName);
            //获取属性的所有值
            ParsedStringTerms attr_value_agg = bucket.getAggregations().get("attr_value_agg");
            List<String> attrValues = attr_value_agg.getBuckets().stream().map(MultiBucketsAggregation.Bucket::getKeyAsString).collect(Collectors.toList());
            attrVo.setAttrValue(attrValues);

            //放到集合
            attrVos.add(attrVo);
        }
        searchResult.setAttrs(attrVos);

        //3、当前商品所涉及的所有品牌信息
        List<SearchResult.BrandVo> brandVos = new ArrayList<>();
        ParsedLongTerms brand_agg = searchResponse.getAggregations().get("brand_agg");
        for (Terms.Bucket bucket : brand_agg.getBuckets()) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            //获取品牌id
            Long brandId = bucket.getKeyAsNumber().longValue();
            brandVo.setBrandId(brandId);
            //获取品牌名称
            ParsedStringTerms brand_name_agg = bucket.getAggregations().get("brand_name_agg");
            String brandName = brand_name_agg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandName(brandName);
            //获取品牌图片
            ParsedStringTerms brand_img_agg = bucket.getAggregations().get("brand_img_agg");
            String brandImg = brand_img_agg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandImg(brandImg);
            //放到集合
            brandVos.add(brandVo);
        }

        searchResult.setBrands(brandVos);

        //4、当前商品所涉及的所有分类信息
        List<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
        ParsedLongTerms catalog_agg = searchResponse.getAggregations().get("catalog_agg");
        for (Terms.Bucket bucket : catalog_agg.getBuckets()) {
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            //获取分类id
            Long catalogId = bucket.getKeyAsNumber().longValue();
            catalogVo.setCatalogId(catalogId);
            //获取分类名称
            ParsedStringTerms catalog_name_agg = bucket.getAggregations().get("catalog_name_agg");
            String catalogName = catalog_name_agg.getBuckets().get(0).getKeyAsString();
            catalogVo.setCatalogName(catalogName);
            //放到集合
            catalogVos.add(catalogVo);
        }
        searchResult.setCatalogs(catalogVos);

        //5、分页信息
        searchResult.setPageNum(searchParam.getPageNum());
        long totalCount = hits.getTotalHits().value;
        searchResult.setTotal(totalCount);
        int totalPages = (int) totalCount % EsConstant.PRODUCT_PAGESIZE == 0 ? (int) (totalCount / EsConstant.PRODUCT_PAGESIZE) : (int) (totalCount / EsConstant.PRODUCT_PAGESIZE + 1);
        searchResult.setTotalPages(totalPages);
        //保存页码
        List<Integer> pageNavs = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            pageNavs.add(i);
        }

        //6、面包屑导航数据
        //6.1、封装所需要的name和value
        if (searchParam.getAttrs() != null && searchParam.getAttrs().size() > 0) {
            List<SearchResult.navVo> navVos = searchParam.getAttrs().stream().map(attr -> {
                SearchResult.navVo navVo = new SearchResult.navVo();
                String[] s = attr.split("_");
                //将已经检索过的id存放进集合
                searchResult.getAttrIds().add(Long.parseLong(s[0]));
                navVo.setNavValue(s[1]);
                R r = productFeignService.getAttrInfo(Long.parseLong(s[0]));
                if (r.getCode() == 0) {
                    AttrResponseVo attrResponseVo = r.getData("attr", new TypeReference<AttrResponseVo>() {
                    });
                    navVo.setNavName(attrResponseVo.getAttrName());
                } else {
                    navVo.setNavName(s[0]);
                }
                //6.2、取消了这个面包屑以后，我们要跳转到那个地方.将请求地址的url当前属性条件置为空值
                //拿到所有的查询条件，去掉当前
                String newUrl = replaceQueryString(searchParam, attr, "attrs");
                navVo.setLink("http://search.gulimall.com/list.html?" + newUrl);
                return navVo;
            }).collect(Collectors.toList());
            searchResult.setNavs(navVos);
        }
        //6.3、将品牌信息放入面包屑导航
        if (searchParam.getBrandId() != null && searchParam.getBrandId().size() > 0) {
            List<SearchResult.navVo> navs = searchResult.getNavs();
            SearchResult.navVo navVo = new SearchResult.navVo();
            navVo.setNavName("品牌");
            String newUrl = "";
            for (Long brandId : searchParam.getBrandId()) {
                for (SearchResult.BrandVo brandVo : searchResult.getBrands()) {
                    if (brandVo.getBrandId().equals(brandId)) {
                        navVo.setNavValue(brandVo.getBrandName());
                        newUrl = replaceQueryString(searchParam, brandId.toString(), "brandId");
                    }
                }
            }
            navVo.setLink("http://search.gulimall.com/list.html?" + newUrl);
            navs.add(navVo);
        }
        searchResult.setPageNavs(pageNavs);
        return searchResult;
    }

    /**
     * 替换字符串
     *
     * @param searchParam 搜索条件
     * @param value 值
     * @param key 键
     * @return 替换后的字符串
     */
    private String replaceQueryString(SearchParam searchParam, String value, String key) {
        String encode = null;
        try {
            encode = URLEncoder.encode(value, "UTF-8");
            //浏览器和java对空格\分号编码不一样
            encode = encode.replace("+", "%20");
            encode = encode.replace("%3B", ";");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return searchParam.getQueryString().replace("&" + key + "=" + encode, "");
    }
}
