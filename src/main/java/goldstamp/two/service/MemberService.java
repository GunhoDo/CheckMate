// front + back/back/main/java/goldstamp/two/service/MemberService.java
package goldstamp.two.service;

import goldstamp.two.domain.Member;
import goldstamp.two.dto.MemberRequestDto;
import goldstamp.two.repository.MemberRepository;
import goldstamp.two.repository.MemberRepositoryClass;
import goldstamp.two.repository.MemoRepository; // MemoRepository 임포트 추가
import goldstamp.two.repository.PrescriptionRepository; // PrescriptionRepository 임포트 추가
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.lang.IllegalArgumentException;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepositoryClass memberRepositoryClass;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final PrescriptionRepository prescriptionRepository; // PrescriptionRepository 주입
    private final MemoRepository memoRepository; // MemoRepository 주입

    //회원 가입
    @Transactional
    public long join(Member member) {
        validateDuplicateMemberID(member); //중복 회원 아이디 검증
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        memberRepositoryClass.save(member); // JpaRepository의 save 메서드 사용
        return member.getId();
    }

    private void validateDuplicateMemberID(Member member) {

        List<Member> findMembers = memberRepository.getWithRoles(member.getLoginId()) != null ?
                List.of(memberRepository.getWithRoles(member.getLoginId())) :
                List.of();

        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원아이디입니다.");
        }
    }

    //회원 전체 조회
    public List<Member> findMembers() {
        return memberRepositoryClass.findAll(); // JpaRepository의 findAll 메서드 사용
    }

    public Member findOne(long id) {
        return memberRepositoryClass.findById(id);
    }

    @Transactional
    public void updatePassword(long id, String passward) {
        Member member = memberRepositoryClass.findById(id);
        member.setPassword(passwordEncoder.encode(passward));
        // @Transactional이 있으므로 save 호출 없이 더티 체킹으로 자동 반영됩니다.
    }

    @Transactional // 이 어노테이션은 여전히 중요합니다!
    public void update(Long id, String name) {
        Member member = memberRepositoryClass.findById(id);// findById 사용
        // if (member == null) { ... } 대신 Optional의 orElseThrow 사용
        member.changeName(name); // Member 클래스에 정의된 changeNickname 메서드를 사용
    }

    @Transactional
    public void updateMember(Long id, MemberRequestDto request) {
        Member member = memberRepositoryClass.findById(id);
        if (member == null) {
            throw new IllegalArgumentException("Invalid member ID");
        }
        if (request.getLoginId() != null) member.setLoginId(request.getLoginId());
        if (request.getName() != null) member.setName(request.getName());
        if (request.getPassword() != null) member.setPassword(passwordEncoder.encode(request.getPassword()));
        if (request.getGender() != null) member.setGender(request.getGender());
        if (request.getBirthDay() != null) member.setBirthDay(request.getBirthDay());
        if (request.getHeight() != 0) member.setHeight(request.getHeight());
        if (request.getWeight() != 0) member.setWeight(request.getWeight());
    }
    // 회원 탈퇴
    @Transactional
    public void deleteMember(Long id) {
        Member member = memberRepositoryClass.findById(id);
        if (member == null) {
            throw new IllegalArgumentException("Invalid member ID");
        }

        // 회원과 연관된 Prescription 삭제 (FK 제약조건 때문에 먼저 삭제)
        prescriptionRepository.deleteByMember_Id(id); // Member ID로 Prescription 삭제
        // 회원과 연관된 Memo 삭제 (FK 제약조건 때문에 먼저 삭제)
        memoRepository.deleteByMember_Id(id); // Member ID로 Memo 삭제

        memberRepository.deleteById(id);
    }
}