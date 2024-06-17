package com.example.spring_homework4.controller;

import com.example.spring_homework5.domain.Account;
import com.example.spring_homework5.domain.Currency;
import com.example.spring_homework5.domain.Customer;
import com.example.spring_homework5.dto.account.AccountDtoRequest;
import com.example.spring_homework5.dto.account.AccountDtoResponse;
import com.example.spring_homework5.dto.customer.CustomerDtoRequest;
import com.example.spring_homework5.dto.customer.CustomerDtoResponse;
import com.example.spring_homework5.mapper.customer.CustomerDtoMapperResponse;
import com.example.spring_homework5.service.DefaultAccountService;
import com.example.spring_homework5.service.DefaultCustomerService;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DefaultCustomerService customerService;
    @MockBean
    private DefaultAccountService accountService;

    @MockBean
    private CustomerDtoMapperResponse customerDtoMapperResponse;

    @Test
    @WithMockUser(username = "testUser")
    void getCustomer() throws Exception {
        Integer customerId = 1;
        Customer customer = new Customer();
        customer.setId(Long.valueOf(customerId));
        customer.setName("Alice");

        CustomerDtoResponse customerDtoResponse = new CustomerDtoResponse();
        customerDtoResponse.setId(Long.valueOf(customerId));
        customerDtoResponse.setName("Alice");

        when(customerService.getOne(Long.valueOf(customerId))).thenReturn(Optional.of(customer));
        when(customerDtoMapperResponse.convertToDto(customer)).thenReturn(customerDtoResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/customers/{id}", customerId))
                .andDo(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    System.out.println("Response Body: " + responseBody);
                })
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(customerId)))
                .andExpect(jsonPath("$.name", Matchers.is("Alice")));
    }



    @Test
    @WithMockUser(username = "testUser")
    void getAllCustomers() throws Exception {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("Alice");

        List<Customer> customers = Collections.singletonList(customer);
        Page<Customer> customerPage = new PageImpl<>(customers, PageRequest.of(0, 10), 1);

        CustomerDtoResponse customerDtoResponse = new CustomerDtoResponse();
        customerDtoResponse.setId(1L);
        customerDtoResponse.setName("Alice");

        when(customerService.findAll(any(Integer.class), any(Integer.class), any(PageRequest.class)))
                .thenReturn(customerPage);
        when(customerDtoMapperResponse.convertToDto(any(Customer.class))).thenReturn(customerDtoResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/customers/all")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", Matchers.is(1)))
                .andExpect(jsonPath("$[0].name", Matchers.is("Alice")));
    }


    @Test
    @WithMockUser(username = "testUser")
    void createCustomer() throws Exception {
        CustomerDtoRequest request = new CustomerDtoRequest();
        request.setName("Alice");
        request.setEmail("alice@example.com");
        request.setAge(25);

        when(customerService.save(any(Customer.class))).thenReturn(new Customer());

        mockMvc.perform(MockMvcRequestBuilders.post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Alice\", \"email\": \"alice@example.com\", \"age\": 25}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "testUser")
    void updateCustomer() throws Exception {
        Long customerId = 1L; // Use Long instead of Integer
        CustomerDtoRequest request = new CustomerDtoRequest();
        request.setName("Alice");
        request.setEmail("alice@example.com");
        request.setAge(25);

        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(customerId);
        updatedCustomer.setName("Alice");
        updatedCustomer.setEmail("alice@example.com");
        updatedCustomer.setAge(25);

        when(customerService.update(eq(customerId), any(Customer.class))).thenReturn(updatedCustomer);

        mockMvc.perform(MockMvcRequestBuilders.put("/customers/id/{id}", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Alice\", \"email\": \"alice@example.com\", \"age\": 25}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(customerId.intValue())))
                .andExpect(jsonPath("$.name", Matchers.is("Alice")))
                .andExpect(jsonPath("$.email", Matchers.is("alice@example.com")))
                .andExpect(jsonPath("$.age", Matchers.is(25)));
    }


    @Test
    @WithMockUser(username = "testUser")
    void deleteCustomer() throws Exception {
        Long customerId = 1L;

        when(customerService.getOne(customerId)).thenReturn(Optional.of(new Customer()));

        mockMvc.perform(MockMvcRequestBuilders.delete("/customers/{id}", customerId))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    @WithMockUser(username = "testUser")
    void createAccountForCustomer() throws Exception {
        Long customerId = 1L;
        String currency = "USD";
        double amount = 100.0;

        Customer customer = new Customer();
        customer.setId(customerId);

        AccountDtoRequest accountDtoRequest = new AccountDtoRequest();
        accountDtoRequest.setCurrency(Currency.valueOf(currency));
        accountDtoRequest.setBalance(amount);

        when(customerService.getOne(customerId)).thenReturn(Optional.of(customer));
        doNothing().when(customerService).createAccountForCustomer(customerId, Currency.valueOf(currency), amount);

        mockMvc.perform(MockMvcRequestBuilders.post("/customers/{id}/accounts", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currency\": \"" + currency + "\", \"balance\": " + amount + "}"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }




    @Test
    @WithMockUser(username = "testUser")
    void deleteAccountFromCustomer() throws Exception {
        Long customerId = 1L;
        UUID accountNumber = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        when(customerService.getOne(customerId)).thenReturn(Optional.of(new Customer()));

        doThrow(new IllegalArgumentException("Account not found")).when(customerService).deleteAccountFromCustomer(customerId, accountNumber);

        mockMvc.perform(MockMvcRequestBuilders.delete("/customers/{customerId}/accounts/{accountNumber}", customerId, accountNumber))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Account with number " + accountNumber + " not found"));
    }
}
