package gulimall.order;

import gulimall.order.entity.OrderEntity;
import gulimall.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class GulimallOrderApplicationTests {
    @Autowired
    private OrderService orderService;

    @Test
    void contextLoads() {
        List<OrderEntity> list = orderService.list();
        System.out.println(list);
    }

}
