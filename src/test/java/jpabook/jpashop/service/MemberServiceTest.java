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

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberService memberService;

    @Autowired
    EntityManager em;

    @Test
    public void signUpTest() throws Exception{
        // Give
        Member member = new Member();
        member.setName("Cho");

        // When
        Long saveId = memberService.join(member);

        // Then
        em.flush();
        assertEquals(member, memberRepository.findOne(saveId));
        
    }

    @Test(expected = IllegalStateException.class)
    public void duplicateUserException() throws Exception{
        // Give
        Member member1 = new Member();
        member1.setName("cho");

        Member member2 = new Member();
        member2.setName("cho");


        // When
        memberService.join(member1);
        memberService.join(member2);
        
        // Then
        fail("예외가 발생해야합니다.");
        
    }
}