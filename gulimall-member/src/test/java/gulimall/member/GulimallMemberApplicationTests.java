package gulimall.member;

import gulimall.member.entity.MemberEntity;
import gulimall.member.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class GulimallMemberApplicationTests {

    @Autowired
    private MemberService memberService;

    @Test
    void contextLoads() {
        List<MemberEntity> memberEntityList = memberService.list();
        System.out.println(memberEntityList);
    }

}
