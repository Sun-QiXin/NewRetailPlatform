package gulimall.auth;



import org.apache.commons.codec.digest.Md5Crypt;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class GulimallAuthServerApplicationTests {

    @Test
    public void testMd5() {
        String code = "123456";
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean matches1 = passwordEncoder.matches(code, "$2a$10$LUfI0DHlVTeJ7iTjK0tNq.Dxa5MHFKDOyhNiz9f1j/UQzHLnuHv0e");
        boolean matches2 = passwordEncoder.matches(code, "$2a$10$rIsLWwZUd7zOmZW3j4eb5eHKt6hRh8dfD7ee5QvvfQB7T54zit.Yi");
        boolean matches3 = passwordEncoder.matches(code, "$2a$10$XFmPrJqmbDOQYZS/i107X.iwcQysKCgDNk6K2hm.hXW5gPrRogLku");
        System.out.println(matches1);
        System.out.println(matches2);
        System.out.println(matches3);
    }

}
