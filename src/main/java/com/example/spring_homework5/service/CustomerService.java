package com.example.spring_homework5.service;

import com.example.spring_homework5.domain.Currency;
import com.example.spring_homework5.domain.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface CustomerService {
    Customer save(Customer obj);
    void delete(Customer obj);
    void deleteAll();
    void saveAll(Customer customer);
    Page<Customer> findAll(Integer from, Integer to, Pageable pageable);
    void deleteById(Long id);
    Optional<Customer> getOne(Long id);
    void createAccountForCustomer(Long id, Currency currency, Double amount);
    void deleteAccountFromCustomer(Long customerId, UUID accountNumber);
    Customer update(Long id, Customer updatedCustomer);
    void assignAccountsToCustomers();
    void deleteCustomerAccounts(Customer customer);
}
