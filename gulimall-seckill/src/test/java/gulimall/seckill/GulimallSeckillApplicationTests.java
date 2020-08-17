package gulimall.seckill;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

class GulimallSeckillApplicationTests {

    @Test
    void contextLoads() {
        LocalDate localDate = LocalDate.now();
        LocalDate localDate1 = localDate.plusDays(2);
        System.out.println(localDate);
        System.out.println(localDate1);

        LocalTime min = LocalTime.MIN;
        LocalTime max = LocalTime.MAX;
        System.out.println(min);
        System.out.println(max);

        String localDateTime1 = LocalDateTime.of(localDate, min).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));;
        String localDateTime2 = LocalDateTime.of(localDate1, max).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));;
        System.out.println(localDateTime1);
        System.out.println(localDateTime2);
    }

}
