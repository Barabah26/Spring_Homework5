package com.example.spring_homework4.service;

import com.example.spring_homework5.dao.AccountJpaRepository;
import com.example.spring_homework5.domain.Account;
import com.example.spring_homework5.service.DefaultAccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultAccountServiceTest {

    @Mock
    private AccountJpaRepository accountJpaRepository;

    @InjectMocks
    private DefaultAccountService accountService;

    @Captor
    private ArgumentCaptor<Long> argumentCaptor;

    @Test
    void save() {
        Account testAccount = new Account();
        testAccount.setBalance(1000.0);

        when(accountJpaRepository.save(testAccount)).thenReturn(testAccount);

        accountService.save(testAccount);
        verify(accountJpaRepository).save(testAccount);
    }

    @Test
    void delete() {
        Account testAccount = new Account();
        testAccount.setBalance(1000.0);
        accountService.delete(testAccount);
        verify(accountJpaRepository).delete(testAccount);
    }

    @Test
    void deleteAll() {
        accountService.deleteAll();
        verify(accountJpaRepository).deleteAll();
    }
    @Test
    void saveAll() {
        Account testAccount1 = new Account();
        testAccount1.setBalance(1000.0);
        Account testAccount2 = new Account();
        testAccount2.setBalance(2000.0);

        accountService.saveAll(testAccount1);
        accountService.saveAll(testAccount2);

        verify(accountJpaRepository).save(testAccount1);
        verify(accountJpaRepository).save(testAccount2);
    }

    @Test
    void findAll() {
        Account account1 = new Account();
        Account account2 = new Account();
        List<Account> accountsExpected = List.of(account1, account2);
        when(accountJpaRepository.findAll()).thenReturn(accountsExpected);

        List<Account> accountsActual = accountService.findAll();
        assertNotNull(accountsActual);
        assertFalse(accountsActual.isEmpty());
        assertIterableEquals(accountsExpected, accountsActual);
    }

    @Test
    void deleteById() {
        Long testId = 1L;
        accountService.deleteById(testId);
        verify(accountJpaRepository).deleteById(argumentCaptor.capture());
        assertEquals(testId, argumentCaptor.getValue(), "Captured ID should match the test ID");
    }

    @Test
    void getOne() {
        Account testAccount = new Account();
        testAccount.setBalance(1000.0);
        when(accountJpaRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        Optional<Account> optionalAccount = accountService.getOne(1L);
        assertTrue(optionalAccount.isPresent(), "Account should be present");
        Account account = optionalAccount.get();
        assertEquals(testAccount.getBalance(), account.getBalance(), "Account balance should match");
    }

    @Test
    void findByNumber() {
        Account testAccount = new Account();
        UUID testNumber = UUID.randomUUID();
        testAccount.setNumber(testNumber);
        when(accountJpaRepository.findByNumber(testNumber)).thenReturn(testAccount);

        Account account = accountService.findByNumber(testNumber);

        assertNotNull(account, "Account should be present");
        assertEquals(testNumber, account.getNumber(), "Account number should match");
    }


    @Test
    void depositToAccount() {
        Account testAccount = new Account();
        UUID testNumber = UUID.randomUUID();
        double initialBalance = 100.0;
        testAccount.setNumber(testNumber);
        testAccount.setBalance(initialBalance);

        double depositAmount = 50.0;

        when(accountJpaRepository.findByNumber(testNumber)).thenReturn(testAccount);

        Account updatedAccount = accountService.depositToAccount(testNumber, depositAmount);

        assertNotNull(updatedAccount, "Account should not be null after deposit");
        assertEquals(initialBalance + depositAmount, updatedAccount.getBalance(), "Balance should be updated correctly");
    }


    @Test
    void withdrawFromAccount() {
        Account testAccount = new Account();
        UUID testNumber = UUID.randomUUID();
        double initialBalance = 100.0;
        testAccount.setNumber(testNumber);
        testAccount.setBalance(initialBalance);

        double withdrawAmount = 50.0;

        when(accountJpaRepository.findByNumber(testNumber)).thenReturn(testAccount);
        boolean updatedAccount = accountService.withdrawFromAccount(testNumber, withdrawAmount);

        assertTrue(updatedAccount, "Withdrawal should be successful");
        assertEquals(initialBalance - withdrawAmount, testAccount.getBalance(), "Balance should be updated correctly");
        verify(accountJpaRepository).updateBalanceByNumber(testNumber, testAccount.getBalance());
    }


    @Test
    void transferMoney() {
        Account fromAccount = new Account();
        Account toAccount = new Account();
        UUID fromAccountNumber = UUID.randomUUID();
        UUID toAccountNumber = UUID.randomUUID();
        double initialBalanceFrom = 100.0;
        double initialBalanceTo = 50.0;
        fromAccount.setNumber(fromAccountNumber);
        fromAccount.setBalance(initialBalanceFrom);
        toAccount.setNumber(toAccountNumber);
        toAccount.setBalance(initialBalanceTo);

        double transferAmount = 50.0;

        when(accountJpaRepository.findByNumber(fromAccountNumber)).thenReturn(fromAccount);
        when(accountJpaRepository.findByNumber(toAccountNumber)).thenReturn(toAccount);

        accountService.transferMoney(fromAccountNumber, toAccountNumber, transferAmount);

        assertEquals(initialBalanceFrom - transferAmount, fromAccount.getBalance(), "Balance of 'from' account should be updated correctly");
        assertEquals(initialBalanceTo + transferAmount, toAccount.getBalance(), "Balance of 'to' account should be updated correctly");

        verify(accountJpaRepository).updateBalanceByNumber(fromAccountNumber, fromAccount.getBalance());
        verify(accountJpaRepository).updateBalanceByNumber(toAccountNumber, toAccount.getBalance());
    }

}