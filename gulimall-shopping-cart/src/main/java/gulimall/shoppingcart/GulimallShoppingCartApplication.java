package gulimall.shoppingcart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author x3626
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class GulimallShoppingCartApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallShoppingCartApplication.class, args);
    }

}
