package gulimall.search;

import com.alibaba.fastjson.JSON;
import gulimall.search.config.ElasticsearchConfig;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


@SpringBootTest
class GulimallSearchApplicationTests {

    @Resource
    private RestHighLevelClient client;

    @Test
    void contextLoads() {
        System.out.println(client);
    }

    @Data
    class user {
        private String username;
        private Integer age;
        private String gender;
    }

    /**
     * 测试存储数据到es
     * 也是更新操作
     */
    @Test
    void insertOrUpdateData() throws IOException {
        IndexRequest indexRequest = new IndexRequest("users");
        indexRequest.id("1");
        //方式1
        //indexRequest.source("username", "孙启新", "age", "21", "gender", "男");

        //方式2
        user user = new user();
        user.setAge(21);
        user.setUsername("孙启新");
        user.setGender("男");
        String jsonString = JSON.toJSONString(user);
        indexRequest.source(jsonString, XContentType.JSON);

        //执行保存操作
        IndexResponse index = client.index(indexRequest, ElasticsearchConfig.COMMON_OPTIONS);
        System.out.println(index);
    }

    /**
     * 测试复杂的检索功能
     */
    @Test
    void searchData() throws IOException {
        //1、创建检索请求
        SearchRequest searchRequest = new SearchRequest();
        //2、指定检索的索引
        searchRequest.indices("newbank");
        //3、检索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //3.1构造检索条件
        searchSourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));
        //3.1.1查看年龄分布
        searchSourceBuilder.aggregation(AggregationBuilders.terms("ageAgg").field("age"));
        //3.1.2查看平均年龄
        searchSourceBuilder.aggregation(AggregationBuilders.avg("balanceAvg").field("balance"));
        //从第几条开始
        searchSourceBuilder.from(0);
        //显示几条
        searchSourceBuilder.size(5);
        System.out.println("检索条件：" + searchSourceBuilder);
        searchRequest.source(searchSourceBuilder);

        //4、执行检索
        SearchResponse searchResponse = client.search(searchRequest, ElasticsearchConfig.COMMON_OPTIONS);
        //打印所有结果
        System.out.println("所有结果：" + searchResponse);

        //5、结果分析
        //5.1获取所有查到的数据
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        System.out.println("所有命中的记录：" + Arrays.toString(searchHits));

        //5.2获取这次检索的分析信息
        Aggregations aggregations = searchResponse.getAggregations();
        Terms ageAgg = aggregations.get("ageAgg");
        for (Terms.Bucket bucket : ageAgg.getBuckets()) {
            System.out.print("年龄："+bucket.getKeyAsString());
            System.out.println("个数："+bucket.getDocCount());
        }
    }
}
