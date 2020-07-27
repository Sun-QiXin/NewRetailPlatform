package gulimall.product;


import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.util.UUID;

/**
 * 使用阿里云的对象存储步骤
 * 1、引入oss-starter
 * 2、在yml文件配置key, endpoint相关信息即可
 * 3、使osSClient进行相关操作
 */
@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 测试redis
     */
    @Test
    void testRedis() {
        //保存
        redisTemplate.opsForValue().set("hello", "word_"+ UUID.randomUUID().toString());

        //查询
        String s = redisTemplate.opsForValue().get("hello");
        System.out.println(s);
    }

    /**
     * 测试Redisson
     */
    @Test
    void testRedisson() {
        System.out.println(redissonClient);
    }
}
