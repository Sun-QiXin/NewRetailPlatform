package gulimall.coupon;

import gulimall.coupon.entity.CouponEntity;
import gulimall.coupon.service.CouponService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class GulimallCouponApplicationTests {
    @Autowired
    private CouponService couponService;

    @Test
    void contextLoads() {
        List<CouponEntity> couponEntityList = couponService.list();
        System.out.println(couponEntityList);
    }
}
