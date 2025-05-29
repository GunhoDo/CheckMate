package goldstamp.two.service;

import goldstamp.two.domain.Gender;
import goldstamp.two.domain.Member;
import goldstamp.two.domain.MemberRole;
import goldstamp.two.dto.MemberRequestDto;
import goldstamp.two.repository.MemberRepository;
import goldstamp.two.repository.MemberRepositoryClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@SpringBootTest
@Transactional // 테스트 후 롤백을 위해 @Transactional 유지
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired MemberRepositoryClass memberRepositoryClass;
    @Autowired PasswordEncoder passwordEncoder; // PasswordEncoder 주입

    @Test
    public void 회원가입() {
        //given
        Member member = new Member();
        member.setLoginId("kim");
        String password = "12345"; // 평문 비밀번호
        member.setPassword(password); // MemberService에서 암호화될 예정
        member.addRole(MemberRole.USER); // 역할 추가 (필수)

        //when
        long savedId = memberService.join(member);

        //then

        // memberRepository.findById()는 Optional을 반환하므로 .get() 또는 .orElseThrow() 사용
        // 여기서는 서비스 계층의 findOne을 사용하는 것이 더 적절합니다.
        Member foundMember = memberService.findOne(savedId); // memberRepositoryImpl.findOne 대신 memberService.findOne 사용
        Assertions.assertEquals(member.getLoginId(), foundMember.getLoginId()); // 객체 비교 대신 필드 비교 권장
        // Assertions.assertEquals(member, foundMember); // 이 비교는 객체 동일성(메모리 주소)을 비교할 수 있어 실패할 수 있습니다.
        // 필드 값 비교가 더 정확합니다.
//        Assertions.assertEquals(member, memberRepository.findById(savedId));

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
        String password = "12345";
        member1.setPassword(password); // 서비스에서 암호화
        member1.addRole(MemberRole.USER);
        long savedId = memberService.join(member1); // 회원 가입

        //when
        String newPassword = "newpassword";
        memberService.updatePassword(savedId, newPassword); // 서비스에서 새 비밀번호 암호화

        //then
        Member member2 = memberService.findOne(savedId); // 변경된 회원 정보 조회
        // 인코딩된 비밀번호는 직접 비교할 수 없으므로, PasswordEncoder의 matches 메서드를 사용
        Assertions.assertTrue(passwordEncoder.matches(newPassword, member2.getPassword()));
    }

    @Test
    public void 닉네임변경() {
        //given
        Member member = new Member();
        member.setLoginId("testuser");
        member.setPassword("password123"); // 서비스에서 암호화
        member.changeName("oldname");
        member.addRole(MemberRole.USER);
        long savedId = memberService.join(member); // 회원 가입

        //when
        String newNickname = "newname";
        memberService.update(savedId, newNickname); // 닉네임 변경 서비스 호출

        //then
        Member updatedMember = memberService.findOne(savedId); // 변경된 회원 정보 조회
        Assertions.assertEquals(newNickname, updatedMember.getName()); // 변경된 닉네임과 일치하는지 확인
    }

    @Test
    public void 회원정보_부분_수정() {
        // given
        Member member = new Member();
        member.setLoginId("initialId");
        String password = "initialPass";
        member.setPassword(password); // 서비스에서 암호화
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
        updateRequest.setGender(Gender.WOMAN); // Gender 변경
        String updatedPassword = "newUpdatedPass";
        updateRequest.setPassword(updatedPassword); // 비밀번호도 업데이트 요청

        memberService.updateMember(savedId, updateRequest);

        // then
        Member updatedMember = memberService.findOne(savedId);

        // Verify updated fields
        Assertions.assertEquals("initialId", updatedMember.getLoginId()); // loginId는 변경 요청에 없으므로 그대로
        Assertions.assertEquals("Updated Name", updatedMember.getName());
        Assertions.assertEquals(Gender.WOMAN, updatedMember.getGender());
        Assertions.assertEquals(LocalDate.of(2000, 1, 1), updatedMember.getBirthDay());
        Assertions.assertEquals(175.5, updatedMember.getHeight(), 0.001); // double 비교 시 오차 허용
        Assertions.assertEquals(60.0, updatedMember.getWeight(), 0.001); // weight는 변경 요청에 없으므로 그대로
        Assertions.assertTrue(passwordEncoder.matches(updatedPassword, updatedMember.getPassword())); // 업데이트된 비밀번호 확인
    }
}