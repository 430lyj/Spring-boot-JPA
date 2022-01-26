package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable // JPA의 내장 타입이기 때문에 내장이 될 수 있는 것
@Getter
public class Address {

    private String city;
    private String street;
    private String zipcode;

    protected Address(){
    } //값 타입을 변경 불가능하게 설계해야 하는데 JPA 스펙 상 만들어 둔 것. 함부로 New로 생성하지 말아야 한다.

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
