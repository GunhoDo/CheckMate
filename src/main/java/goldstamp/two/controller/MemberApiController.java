package goldstamp.two.controller;

import goldstamp.two.domain.Member;
import goldstamp.two.service.MemberService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

//JSON 형태의 REST API 응답 생성
@RestController
//memberService를 생성자 주입
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService; //서비스 계층을 통해 비즈니스 로직 수행

    @GetMapping("/members")
    //회원 전체 조회
    public Result members() {
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());
        return new Result(collect);
    }

    //회원 생성

    @PostMapping("/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setLoginId(request.getLoginId());
        member.setName(request.getName());
        member.setPassword(request.getPassword());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    //회원 정보 수정
    @PutMapping("/members/{id}")
    public UpdateMemberResponse updateMemberResponseV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request) {
        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    //응답 데이터를 감싸는 wrapper
    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }

    @Data
    static class MemberDto {
        private String name;

        public MemberDto(String name) {
            this.name = name;
        }
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    @Data
    static class CreateMemberRequest {
        private String loginId;
        private String name;
        private String password;
    }

    @Data
    public class CreateMemberResponse {
        private Long id;

        // Long id를 받는 생성자만 정의
        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }
}
