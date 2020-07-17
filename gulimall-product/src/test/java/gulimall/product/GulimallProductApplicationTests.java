package gulimall.product;

import gulimall.product.entity.BrandEntity;
import gulimall.product.service.BrandService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

/**
 * 使用阿里云的对象存储步骤
 * 1、引入oss-starter
 * 2、在yml文件配置key, endpoint相关信息即可
 * 3、使osSClient进行相关操作
 */
@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    private BrandService brandService;

    @Test
    void contextLoads() {
        List<BrandEntity> brandEntities = brandService.list();
        System.out.println(brandEntities);
    }
}
