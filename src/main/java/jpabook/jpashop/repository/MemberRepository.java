package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository //Spring bean으로 등록
@RequiredArgsConstructor //em 주입
public class MemberRepository {

    //@PersistenceContext //표준 애노테이션; 스프링이 자동으로 엔티티 매니저를 생성해서 주입을 해줌.
    private EntityManager em; //스프링부트(스프링 데이터 JPA)가 @Autowired도 이해할 수 있도록 지원을 해줌 -> 따라서 생성자 주입, 롬복 모두 사용 가능

    public void save(Member member){
        em.persist(member); //영속성 컨텍스트에 멤버 엔티티를 넣음 -> 트랜잭션이 커밋 되는 시점에 Insert 쿼리문이 날아감.
    }

    public Member findOne(Long id){
        return em.find(Member.class, id); //단건 조회
    }

    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();  //JPQL 사용 (SQL과 유사하지만 SQL은 테이블 단위, JPQL은 엔티티 단위)
    }

    public List<Member> findByName(String name){
        return em.createQuery("selct m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList(); // 파라미터 바인딩
    }
}
