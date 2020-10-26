package gulimall.ware;

import gulimall.ware.entity.WareInfoEntity;
import gulimall.ware.service.WareInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class GulimallWareApplicationTests {

    @Autowired
    private WareInfoService wareInfoService;

    @Test
    void contextLoads() {
        List<WareInfoEntity> list = wareInfoService.list();
        System.out.println(list);
    }

}
