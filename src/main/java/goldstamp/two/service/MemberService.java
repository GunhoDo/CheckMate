package goldstamp.two.service;

import goldstamp.two.domain.Member;
import goldstamp.two.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.lang.IllegalArgumentException;
import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    //회원 가입
    @Transactional
    public long join(Member member) {
        validateDuplicateMemberID(member); //중복 회원 아이디 검증
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMemberID(Member member) {
        List<Member> findMembers = memberRepository.findByLoginId(member.getLoginId());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원아이디입니다.");
        }

    }
    //회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findOne(long id) {
        return memberRepository.findOne(id);
    }

    @Transactional
    public void updatePassword(long id, String passward) {
        Member member = memberRepository.findOne(id);
        member.setPassword(passward);
    }

    public void update(Long id, String name) {
            Member member = memberRepository.findOne(id);
            if (member == null) {
                throw new IllegalArgumentException("Invalid member ID");
            }
            member.setName(name);  // name 수정
            memberRepository.save(member);  // 수정된 멤버 저장
    }
}
