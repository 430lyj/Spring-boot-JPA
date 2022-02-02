package jpabook.jpashop.api;


import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@RestController //@Controller랑 @ResponseBody 합친 것 -> REST API에 적합
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/api/v1/members") //엔티티 외부 노출 방법 -> 권장하지 않음!
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){ //json으로 온 바디를 Member 객체에 그대로 매핑해줌.
        //@Valid를 사용하면 validation 로직이 entity (NotEmpty) 에 있음. + 엔티티의 필드 명을 바꾸면 api 스펙 자체가 바뀌게 됨. -> DTO 생성 필요!
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){ //별도의 DTO로 만들면 api 스펙이 바뀌지 않음.
        Member member = new Member();
        member.setName(request.getName());
        member.setAddress(new Address(request.getCity(), request.getStreet(), request.getZipcode()));
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
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
}