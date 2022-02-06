package jpabook.jpashop.api;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@RestController //@Controller랑 @ResponseBody 합친 것 -> REST API에 적합
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    //엔티티 외부 노출 방법 -> 권장하지 않음!
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){ //json으로 온 바디를 Member 객체에 그대로 매핑해줌.
        //@Valid를 사용하면 validation 로직이 entity (NotEmpty) 에 있음. + 엔티티의 필드 명을 바꾸면 api 스펙 자체가 바뀌게 됨. -> DTO 생성 필요!
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @Operation(summary = "test hello", description = "hello api example")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK !!"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST !!"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND !!"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR !!")
    })
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){ //별도의 DTO로 만들면 api 스펙이 바뀌지 않음.
        Member member = new Member();
        member.setName(request.getName());
        member.setAddress(new Address(request.getCity(), request.getStreet(), request.getZipcode()));
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberResponseV2(@PathVariable("id") Long id,
                                                       @RequestBody @Valid UpdateMemberRequest request){
        memberService.update(id, request.getName()); //command와 query는 분리하는 게 좋으므로 command는 update 함수 내에서 처리 + controller에서 쿼리로 이후 조회해줌.
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        return memberService.findMembers(); //entity 직접 노출 -> 원하는 정보만 보여주려면 entity에 직접 @JsonIgnore 해줘야 함.
    }

    @GetMapping("/api/v2/members")
    public Result membersV2(){
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());
        return new Result(collect.size(), collect);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count;
        private T data;
    }

    @Data
    @AllArgsConstructor
    static  class MemberDto {
        private String name;
    }
    @Data
    static class CreateMemberRequest{
        @NotEmpty
        private String name;
        private String city;
        private String street;
        private String zipcode;
    }

    @Data
    static class CreateMemberResponse{
        private Long id;

        public CreateMemberResponse(Long id){
            this.id = id;
        }
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    private class UpdateMemberResponse {
        private Long id;
        private String name;
    }
}