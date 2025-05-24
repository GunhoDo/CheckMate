package goldstamp.two.service;

import goldstamp.two.domain.Member;
import goldstamp.two.repository.MemberRepository; // MemberRepositoryImpl 대신 MemberRepository 인터페이스 import
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.lang.IllegalArgumentException;
import java.util.List;
import java.util.Optional; // findById 사용을 위해 Optional import

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    //회원 가입
    @Transactional
    public long join(Member member) {
        validateDuplicateMemberID(member); //중복 회원 아이디 검증
        memberRepository.save(member); // JpaRepository의 save 메서드 사용
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
        return memberRepository.findAll(); // JpaRepository의 findAll 메서드 사용
    }

    public Member findOne(long id) {
        // JpaRepository는 findById를 반환하며 Optional을 반환합니다.
        // Optional.orElseThrow()를 사용하여 값이 없으면 예외를 발생시킬 수 있습니다.
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID: " + id));
    }

    @Transactional
    public void updatePassword(long id, String passward) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID: " + id));
        member.setPassword(passward);
        // @Transactional이 있으므로 save 호출 없이 더티 체킹으로 자동 반영됩니다.
    }

    @Transactional // 이 어노테이션은 여전히 중요합니다!
    public void update(Long id, String name) {
        Member member = memberRepository.findById(id) // findById 사용
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID: " + id));
        // if (member == null) { ... } 대신 Optional의 orElseThrow 사용
        member.changeName(name); // Member 클래스에 정의된 changeNickname 메서드를 사용

    }
}

