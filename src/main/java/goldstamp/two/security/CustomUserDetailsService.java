package goldstamp.two.security;

import goldstamp.two.domain.Member;
import goldstamp.two.dto.MemberDto;
import goldstamp.two.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Log4j2
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("------------------loadUserByUsername 시작: 사용자 ID (username) = " + username + " -------------------");

        Member member = memberRepository.getWithRoles(username);

        if(member == null) {
            log.warn("사용자를 찾을 수 없습니다: " + username);
            throw new UsernameNotFoundException("User not found with username: " + username); // 예외 메시지를 좀 더 명확하게
        }

        // --- 로그 추가 1: DB에서 가져온 Member 객체의 비밀번호(해시값) 확인 ---
        log.info("DB에서 가져온 Member 객체: " + member); // Member 객체 전체 로깅 (toString() 구현 필요)
        log.info("DB에서 가져온 member의 password (해시값이어야 함): " + member.getPassword());
        // ----------------------------------------------------------------

        MemberDto memberDTO = new MemberDto(
                member.getLoginId(),
                member.getPassword(), // 이 값이 DB의 해시값이어야 함
                member.getName(),
                member.isSocial(),
                member.getMemberRoleList()
                        .stream()
                        .map(memberRole -> memberRole.name()).collect(Collectors.toList()));

        // --- 로그 추가 2: UserDetails(MemberDto)에 담긴 비밀번호(해시값) 확인 ---
        // MemberDto의 toString() 메소드가 password 필드를 포함하거나,
        // memberDTO.getPassword()를 직접 호출하여 확인합니다.
        // Lombok의 @ToString 사용 시 exclude 하지 않았다면 기본 포함됩니다.
        log.info("생성된 MemberDto (UserDetails) 객체: " + memberDTO);
        log.info("UserDetails(MemberDto)에 담긴 password (해시값이어야 함): " + memberDTO.getPassword());
        // --------------------------------------------------------------------

        log.info("------------------loadUserByUsername 종료: " + username + "에 대한 UserDetails 반환 -------------------");
        return memberDTO;
    }
}