package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @NotEmpty
    private String name;

    @Embedded //내장 타입 사용; Embeddable이나 Embedded 둘 중에 하나만 사용해도 작동하지만 명시하기 위해서 둘다 사용하는 경우가 많음
    private Address address;

    @OneToMany(mappedBy = "member") //나는 이 연관관계의 주인이 아니다라는 뜻; Order table에 있는 member에 연결된 거울일 뿐 + 읽기 전용으로 변함
    private List<Order> orders = new ArrayList<>(); //이렇게 해두면 null pointer exception 날 일이 없다.
    // 또, 하이버네이트는 엔티티를 영속할 때(DB에 저장하개 되면) 컬렉션을 감싸서 하이버네이트가 제공하는 내장 컬렉션으로 변경됨. 만약 생성자나 getter에서 만지게 되면 하이버네이트 내부 매커니즘에서 문제가 생길수도 있다.

}
