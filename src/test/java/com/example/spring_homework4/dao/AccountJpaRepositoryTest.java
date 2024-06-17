package com.example.spring_homework4.dao;

import com.example.spring_homework5.dao.AccountJpaRepository;
import com.example.spring_homework5.domain.Account;
import com.example.spring_homework5.service.DefaultAccountService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.event.RecordApplicationEvents;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DataJpaTest
class AccountJpaRepositoryTest {

    @MockBean
    private AccountJpaRepository accountJpaRepository;

    @Autowired
    private DefaultAccountService accountService;

    @Test
    void findByNumber() {
        UUID accountNumber = UUID.fromString("619bc356-6f93-42da-a422-f7df335529a8");
        Account account = new Account();
        given(accountJpaRepository.findByNumber(accountNumber)).willReturn(account);
        Account foundAccount = accountService.findByNumber(accountNumber);
        assertThat(foundAccount).isEqualTo(account);
    }
}
