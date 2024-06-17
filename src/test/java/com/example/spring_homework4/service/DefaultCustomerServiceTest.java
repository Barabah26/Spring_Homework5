package com.example.spring_homework4.service;

import com.example.spring_homework5.dao.AccountJpaRepository;
import com.example.spring_homework5.dao.CustomerJpaRepository;
import com.example.spring_homework5.domain.Account;
import com.example.spring_homework5.domain.Currency;
import com.example.spring_homework5.domain.Customer;
import com.example.spring_homework5.service.DefaultCustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultCustomerServiceTest {

    @Mock
    private CustomerJpaRepository customerJpaRepository;

    @Mock
    private AccountJpaRepository accountJpaRepository;

    @InjectMocks
    private DefaultCustomerService customerService;

    @Captor
    private ArgumentCaptor<Long> argumentCaptor;


    @Test
    void save() {
        Customer testCustomer = new Customer();
        testCustomer.setName("John Doe");

        when(customerJpaRepository.save(testCustomer)).thenReturn(testCustomer);

        customerService.save(testCustomer);
        verify(customerJpaRepository).save(testCustomer);
    }

    @Test
    void delete() {
        Customer testCustomer = new Customer();
        testCustomer.setName("John Doe");
        customerService.delete(testCustomer);
        verify(customerJpaRepository).delete(testCustomer);
    }

    @Test
    void deleteAll() {
        customerService.deleteAll();
        verify(customerJpaRepository).deleteAll();
    }

    @Test
    void saveAll() {
        Customer testCustomer1 = new Customer();
        testCustomer1.setName("John Doe");
        Customer testCustomer2 = new Customer();
        testCustomer2.setName("Jane Smith");

        customerService.saveAll(testCustomer1);
        customerService.saveAll(testCustomer2);

        verify(customerJpaRepository).save(testCustomer1);
        verify(customerJpaRepository).save(testCustomer2);
    }

    @Test
    void findAll() {
        Customer testCustomer1 = new Customer();
        Customer testCustomer2 = new Customer();
        List<Customer> customersExpected = List.of(testCustomer1, testCustomer2);

        Page<Customer> page = new PageImpl<>(customersExpected);
        when(customerJpaRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);
        Page<Customer> customerActual = customerService.findAll(0, Integer.MAX_VALUE, PageRequest.of(0, 10));
        assertNotNull(customerActual);
        assertFalse(customerActual.isEmpty());
        assertIterableEquals(customersExpected, customerActual.getContent());
    }

    @Test
    void deleteById() {
        Long testId = 1L;
        customerService.deleteById(testId);
        verify(customerJpaRepository).deleteById(testId);
    }

    @Test
    void getOne() {
        Customer testCustomer = new Customer();
        testCustomer.setName("John Doe");
        when(customerJpaRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        Optional<Customer> optionalCustomer = customerService.getOne(1L);
        assertTrue(optionalCustomer.isPresent(), "Customer should be present");
        Customer customer = optionalCustomer.get();
        assertEquals(testCustomer.getName(), customer.getName(), "Customer names should match");
    }

    @Test
    void createAccountForCustomer() {
        Customer testCustomer = new Customer();
        testCustomer.setId(1L);
        when(customerJpaRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        Currency currency = Currency.USD;
        Double amount = 100.0;

        customerService.createAccountForCustomer(1L, currency, amount);
        verify(accountJpaRepository, times(1)).save(any(Account.class));

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountJpaRepository).save(accountCaptor.capture());
        Account savedAccount = accountCaptor.getValue();
        assertEquals(currency, savedAccount.getCurrency());
        assertEquals(amount, savedAccount.getBalance());
        assertEquals(testCustomer, savedAccount.getCustomer());
    }

    @Test
    void deleteCustomerAccounts() {
        Customer testCustomer = new Customer();
        Account testAccount1 = new Account();
        testAccount1.setId(1L);
        Account testAccount2 = new Account();
        testAccount2.setId(2L);
        testCustomer.setAccounts(List.of(testAccount1, testAccount2));

        customerService.deleteCustomerAccounts(testCustomer);

        verify(accountJpaRepository, times(2)).deleteById(anyLong());
    }

    @Test
    void deleteAccountFromCustomer() {
        Long customerId = 1L;
        UUID accountNumber = UUID.randomUUID();
        Customer testCustomer = new Customer();
        testCustomer.setId(customerId);
        Account testAccount = new Account();
        testAccount.setNumber(accountNumber);
        testCustomer.setAccounts(List.of(testAccount));
        when(customerJpaRepository.getOne(customerId)).thenReturn(testCustomer);
        customerService.deleteAccountFromCustomer(customerId, accountNumber);
        verify(accountJpaRepository).delete(testAccount);
        verify(customerJpaRepository, times(1)).getOne(customerId);
    }


    @Test
    void update() {
        Customer testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("John Doe");
        testCustomer.setEmail("john.doe@example.com");
        testCustomer.setAge(30);

        Customer updatedCustomer = new Customer();
        updatedCustomer.setName("Jane Smith");
        updatedCustomer.setEmail("jane.smith@example.com");
        updatedCustomer.setAge(35);

        when(customerJpaRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

        Customer result = customerService.update(1L, updatedCustomer);

        assertNotNull(result);
        assertEquals(updatedCustomer.getName(), result.getName());
        assertEquals(updatedCustomer.getEmail(), result.getEmail());
        assertEquals(updatedCustomer.getAge(), result.getAge());
    }



    @Test
    void assignAccountsToCustomers() {
        List<Account> accounts = List.of(mock(Account.class), mock(Account.class));
        when(accountJpaRepository.findAll()).thenReturn(accounts);

        customerService.assignAccountsToCustomers();

        for (Account account : accounts) {
            verify(account, times(1)).getCustomer();
        }
    }
}
