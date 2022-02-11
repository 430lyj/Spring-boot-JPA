package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter @Setter
public class Delivery {

    @Id @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @JsonIgnore
    @OneToOne(mappedBy = "delivery", fetch = LAZY)
    private Order order;

    @Enumerated(EnumType.STRING) //ORDINAL은 숫자 0, 1, 2 타입; 0, 1, 2로 넣으면 중간에 enum 상태 추가하면 에러 나기 쉬움.
    private DeliveryStatus status; //READY, COMP

    @Embedded
    private Address address;
}
