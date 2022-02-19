package jpabook.jpashop.repository;

import jpabook.jpashop.api.OrderSimpleApiController;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;

    public void save(Order order) {em.persist(order);}

    public Order findOne(Long id){
        return em.find(Order.class, id);
    }

    public List<Order> findAllByString(OrderSearch orderSearch) {
        //language=JPQL
        String jpql = "select o From Order o join o.member m";
        boolean isFirstCondition = true;
//주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }
//회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }
        TypedQuery<Order> query = em.createQuery(jpql, Order.class) .setMaxResults(1000); //최대 1000건
        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();
    }

    /**
     * JPA Criteria
     */
    public List<Order> findAllByCriteria (OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Order, Member> m = o.join("member", JoinType.INNER); //회원과 조인
        List<Predicate> criteria = new ArrayList<>();
//주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"),
                    orderSearch.getOrderStatus());
            criteria.add(status);
        }
//회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name =
                    cb.like(m.<String>get("name"), "%" +
                            orderSearch.getMemberName() + "%");
            criteria.add(name);

        }
        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //최대 1000건
        return query.getResultList();
    }

    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" + //order 가져올 때 한 방에 member도 가져오고 싶다
                        " join fetch o.delivery d", Order.class //order 가져올 때 한 방에 delivery도 가져오고 싶다
        ).getResultList();
    }

    public List<Order> findAllWithItem(){
        return em.createQuery(
                "select distinct o from Order o " + //DB에 distinct 넣은 것과 동일. but distinct는 한 줄(row)이 다 똑같아야만 중복을 제외하는 것이 작동하는데,
                        // 여기서는 그런 경우가 없어서 작동되지 않음.
                        " join fetch o.member m " +
                        " join fetch o.delivery d " +
                        " join fetch o.orderItems oi " +
                        " join fetch oi.item i", Order.class)
//                .setFirstResult(1)  //1대다 fetch join 하는 경우 페이징 접근 방식이 전체를 다 끌어 올린 후 메모리에서 나누는 방식 (lazy loading 아님)
//                .setMaxResults(100) //따라서 일대다 fetch join은 페이징 하면 메모리 오버 플로우 발생 -> 불가능하다고 보면 됨.
                .getResultList();
    }
}
