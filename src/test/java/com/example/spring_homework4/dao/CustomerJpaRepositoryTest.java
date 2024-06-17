package com.example.spring_homework4.dao;

import com.example.spring_homework5.dao.CustomerJpaRepository;
import com.example.spring_homework5.domain.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.event.RecordApplicationEvents;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@DataJpaTest
class CustomerJpaRepositoryTest {

    @MockBean
    private CustomerJpaRepository customerJpaRepository;

    @Autowired
    private CustomerJpaRepository customerRepository;

    @Test
    void updateCustomerById() {
        Long customerId = 1L;
        String newName = "Updated Name";
        String newEmail = "updated@email.com";
        Integer newAge = 30;
        customerRepository.updateCustomerById(customerId, newName, newEmail, newAge);
        verify(customerJpaRepository).updateCustomerById(customerId, newName, newEmail, newAge);
    }
}
