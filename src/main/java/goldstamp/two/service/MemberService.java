package goldstamp.two.service;

import goldstamp.two.domain.Member;
import goldstamp.two.dto.MemberRequestDto;
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
        return memberRepository.findById(id);
    }

    @Transactional
    public void updatePassword(long id, String passward) {
        Member member = memberRepository.findById(id);
        member.setPassword(passward);
    }

    @Transactional
    public void update(Long id, MemberRequestDto request) {
        Member member = memberRepository.findById(id);
        if (member == null) {
            throw new IllegalArgumentException("Invalid member ID");
        }
        if (request.getLoginId() != null) member.setLoginId(request.getLoginId());
        if (request.getName() != null) member.setName(request.getName());
        if (request.getPassword() != null) member.setPassword(request.getPassword());
        if (request.getGender() != null) member.setGender(request.getGender());
        if (request.getBirthDay() != null) member.setBirthDay(request.getBirthDay());
        if (request.getHeight() != 0) member.setHeight(request.getHeight());
        if (request.getWeight() != 0) member.setWeight(request.getWeight());
    }
}
