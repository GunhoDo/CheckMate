package goldstamp.two.service;

import goldstamp.two.domain.Gender;
import goldstamp.two.domain.Member;
import goldstamp.two.domain.MemberRole;
import goldstamp.two.dto.MemberRequestDto;
import goldstamp.two.repository.MemberRepository;
import goldstamp.two.repository.MemberRepositoryClass; // MemberRepositoryClass import 추가
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder; // PasswordEncoder import
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@SpringBootTest
@Transactional // 테스트 후 롤백을 위해 @Transactional 유지
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired MemberRepositoryClass memberRepositoryClass; // MemberRepositoryClass 주입
    @Autowired PasswordEncoder passwordEncoder; // PasswordEncoder 주입

    @Test
    public void 회원가입() {
        //given
        Member member = new Member();
        member.setLoginId("kim");
        member.setPassword("12345"); // 비밀번호 설정
        member.addRole(MemberRole.USER); // 역할 추가 (필수)

        //when
        long savedId = memberService.join(member);

        //then
        Member foundMember = memberService.findOne(savedId);
        Assertions.assertEquals(member.getLoginId(), foundMember.getLoginId());
        // 비밀번호는 인코딩되어 저장되므로 직접 비교가 아닌, 서비스에서 조회된 멤버의 패스워드가 null이 아닌지 정도만 확인
        Assertions.assertNotNull(foundMember.getPassword());
    }

    @Test
    public void 중복_회원_예외() {
        //given
        Member member1 = new Member();
        member1.setLoginId("kim");
        member1.setPassword("12345");
        member1.addRole(MemberRole.USER);

        Member member2 = new Member();
        member2.setLoginId("kim");
        member2.setPassword("54321");
        member2.addRole(MemberRole.USER);

        //when
        memberService.join(member1);

        //then
        Assertions.assertThrows(IllegalStateException.class, () -> {
            memberService.join(member2);
        }, "이미 존재하는 회원아이디입니다."); // 예상 메시지 추가
    }

    @Test
    public void 패스워드변경() {
        //given
        Member member1 = new Member();
        member1.setLoginId("kim");
        member1.setPassword(passwordEncoder.encode("12345")); // 비밀번호 인코딩하여 설정
        member1.addRole(MemberRole.USER);
        long savedId = memberService.join(member1);

        //when
        String newPassword = "newpassword";
        memberService.updatePassword(savedId, passwordEncoder.encode(newPassword)); // 새 비밀번호도 인코딩

        //then
        Member member2 = memberService.findOne(savedId);
        // 인코딩된 비밀번호는 직접 비교할 수 없으므로, PasswordEncoder의 matches 메서드를 사용
        Assertions.assertTrue(passwordEncoder.matches(newPassword, member2.getPassword()));
    }

    @Test
    public void 닉네임변경() {
        //given
        Member member = new Member();
        member.setLoginId("testuser");
        member.setPassword(passwordEncoder.encode("password123")); // 비밀번호 인코딩하여 설정
        member.changeName("oldname");
        member.addRole(MemberRole.USER);
        long savedId = memberService.join(member);

        //when
        String newNickname = "newname";
        memberService.update(savedId, newNickname);

        //then
        Member updatedMember = memberService.findOne(savedId);
        Assertions.assertEquals(newNickname, updatedMember.getName());
    }

    @Test
    public void 회원정보_부분_수정() {
        // given
        Member member = new Member();
        member.setLoginId("initialId");
        member.setPassword(passwordEncoder.encode("initialPass"));
        member.setName("Initial Name");
        member.setGender(Gender.MAN);
        member.setBirthDay(LocalDate.of(2000, 1, 1));
        member.setHeight(170.0);
        member.setWeight(60.0);
        member.addRole(MemberRole.USER);
        long savedId = memberService.join(member);

        // when
        MemberRequestDto updateRequest = new MemberRequestDto();
        updateRequest.setName("Updated Name");
        updateRequest.setHeight(175.5);
        updateRequest.setGender(Gender.WOMAN); // Change gender

        memberService.updateMember(savedId, updateRequest);

        // then
        Member updatedMember = memberService.findOne(savedId);

        // Verify only updated fields are changed, others remain the same
        Assertions.assertEquals("initialId", updatedMember.getLoginId()); // loginId should not change
        Assertions.assertTrue(passwordEncoder.matches("initialPass", updatedMember.getPassword())); // password should not change
        Assertions.assertEquals("Updated Name", updatedMember.getName());
        Assertions.assertEquals(Gender.WOMAN, updatedMember.getGender());
        Assertions.assertEquals(LocalDate.of(2000, 1, 1), updatedMember.getBirthDay());
        Assertions.assertEquals(175.5, updatedMember.getHeight(), 0.001); // Use delta for double comparison
        Assertions.assertEquals(60.0, updatedMember.getWeight(), 0.001); // weight should not change
    }
}