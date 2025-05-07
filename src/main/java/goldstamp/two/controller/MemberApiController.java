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

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

//    @GetMapping("/members")
//    public Result members() {
//        List<Member> findMembers = memberService.findMembers();
//        List<MemberDto> collect = findMembers.stream()
//                .map(m -> new MemberDto(m.getName()))
//                .collect(Collectors.toList());
//        return new Result(collect);
//    }
//
//    @PostMapping("/members")
//    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
//        Member member = new Member();
//        member.setName(request.getName());
//        Long id = memberService.join(member);
//        return new CreateMemberResponse(id);
//        //Entity의 필드 이름을 바꿔도 컴파일에러가 난다
//        //별도의 DTO -> Entity를 변경해도 API 스펙이 바뀌지 않는다 -> 어느 API 스펙이 오는지 확실하게 알 수 있다는 장점
//    }

//    @PutMapping("/members/{id}")
//    public UpdateMemberResponse updateMemberResponseV2(
//            @PathVariable("id") Long id,
//            @RequestBody @Valid UpdateMemberRequest request) {
//        memberService.update(id, request.getName());
//        Member findMember = memberService.findOne(id);
//        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
//    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
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
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }
}
