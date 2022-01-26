package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded //내장 타입 사용; Embeddable이나 Embedded 둘 중에 하나만 사용해도 작동하지만 명시하기 위해서 둘다 사용하는 경우가 많음
    private Address address;

    @OneToMany(mappedBy = "member") //나는 이 연관관계의 주인이 아니다라는 뜻; Order table에 있는 member에 연결된 거울일 뿐 + 읽기 전용으로 변함
    private List<Order> orders = new ArrayList<>();
}
