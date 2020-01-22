package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;

import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    public void orderGoods() throws Exception{
        // Give
        Member member = createMember("회원1", new Address("서울", "강가", "111-000"));

        Book book = createBook("시골 JPA", 10000, 10);

        int orderCount = 2;

        // When
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        // Then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("상품주문시 상태는 OREDER", OrderStatus.ORDER, getOrder.getStatus());
        assertEquals("주문한 상품 종류 수가 정확해야 한다.", 1, getOrder.getOrderItems().size());
        assertEquals("주문 가격은 가격 * 수량이다.", 10000*orderCount, getOrder.getTotalPrice());
        assertEquals("주문 수량만큼 재고가 줄어야한다.", 8, book.getStockQuantity());
    }

    @Test
    public void cancelOrder() throws Exception{
        // Give
        Member member = createMember("회원1", new Address("서울", "강가", "111-000"));
        Book item = createBook("시골 JPA", 10000, 10);

        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        // When
        orderService.cancelOrder(orderId);

        // Then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("주문 취소시 상태는 cancel", OrderStatus.CANCEL, getOrder.getStatus());
        assertEquals("주문 취소시 재고는 원복", 10, item.getStockQuantity());

    }

    @Test(expected = NotEnoughStockException.class)
    public void overGoodsValidation() throws Exception{
        // Give
        Member member = createMember("회원1", new Address("서울", "강가", "111-000"));
        Book book = createBook("시골 JPA", 10000, 10);
        int orderCount = 11;

        // When
        orderService.order(member.getId(), book.getId(), orderCount);

        // Then
        fail("재고 부족 에러가 발생해야한다.");

    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember(String name, Address address) {
        Member member = new Member();
        member.setName(name);
        member.setAddress(address);
        em.persist(member);
        return member;
    }
}