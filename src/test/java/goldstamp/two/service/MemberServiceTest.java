package goldstamp.two.service;


import goldstamp.two.domain.Member;
import goldstamp.two.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;

    @Test
    public void 회원가입() {
        //given
        Member member = new Member();
        member.setLoginId("kim");
        //when
        long savedId = memberService.join(member);
        //then
        Assertions.assertEquals(member, memberRepository.findById(savedId));
    }

    @Test
    public void 중복_회원_예외() {
        //given
        Member member1 = new Member();
        member1.setLoginId("kim");

        Member member2 = new Member();
        member2.setLoginId("kim");
        //when
        memberService.join(member1);

        //then
        Assertions.assertThrows(IllegalStateException.class, () -> {
            memberService.join(member2);
        });
    }

    @Test
    public void 패스워드변경() {
        //given
        Member member1 = new Member();
        member1.setLoginId("kim");
        member1.setPassword("12345");
        long savedId = memberService.join(member1);
        //when
        memberService.updatePassword(savedId, "54321");
        Member member2 = memberService.findOne(savedId);
        //then
        Assertions.assertEquals(member2.getPassword(), "54321");
    }
}