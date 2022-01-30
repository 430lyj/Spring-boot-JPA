package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class) //JUnit5되면서 사라짐.
@SpringBootTest  // 이 두 가지가 있어야 스프링에서도 잘되는지 까지 테스트 가능
@Transactional
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;

    @Test
    //@Rollback(false)
    public void 회원가입() throws Exception {
        //given
        Member member = new Member();
        member.setName("kimmmmmm");

        //when
        Long savedId = memberService.join(member);
        em.persist(member);

        //then
        em.flush(); //DB에 쿼리를 날리는 것까지 볼 수 있음.
        assertEquals(member, memberRepository.findOne(savedId));
    } //JPA의 경우 em.persist()한 경우에 바로 쿼리가 날아가는 게 아니고 transaction이 커밋될 때 한꺼번에 날아감
      // -> 테스트의 transactional 은 커밋이 되지 않으므로 insert문도 날아가지 않음!

    @Test(expected = IllegalStateException.class)
    public void 중복회원예외() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        //when
        memberService.join(member1);
        memberService.join(member2);

        //then
//        fail("예외");
    }
}