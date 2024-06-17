package com.example.spring_homework4.controller;

import com.example.spring_homework5.domain.Account;
import com.example.spring_homework5.domain.Currency;
import com.example.spring_homework5.dto.account.AccountDtoResponse;
import com.example.spring_homework5.mapper.account.AccountDtoMapperResponse;
import com.example.spring_homework5.service.DefaultAccountService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DefaultAccountService accountService;

    @MockBean
    private AccountDtoMapperResponse accountDtoMapperResponse;

    @Test
    @WithMockUser(username = "testUser")
    void getAllAccounts() throws Exception {
        Account account = new Account();

        AccountDtoResponse accountDtoResponse = new AccountDtoResponse();
        accountDtoResponse.setCurrency(Currency.USD);
        accountDtoResponse.setBalance(1000.0);

        when(accountService.findAll()).thenReturn(List.of(account));
        when(accountDtoMapperResponse.convertToDto(account)).thenReturn(accountDtoResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/all").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].currency", Matchers.is(Currency.USD.toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].balance", Matchers.is(1000.0)));
    }

    @Test
    @WithMockUser(username = "testUser")
    void deleteAccountById() throws Exception {
        Long id = 2L;
        doNothing().when(accountService).deleteById(id);
        mockMvc.perform(MockMvcRequestBuilders.delete("/accounts/{id}", id))
                .andExpect(status().isNoContent());
        verify(accountService, times(1)).deleteById(id);
    }

    @Test
    @WithMockUser(username = "testUser")
    void getAccountById() throws Exception {
        Account account = new Account();

        AccountDtoResponse accountDtoResponse = new AccountDtoResponse();
        Long id = 2L;
        accountDtoResponse.setId(id);

        when(accountService.getOne(id)).thenReturn(Optional.of(account));
        when(accountDtoMapperResponse.convertToDto(account)).thenReturn(accountDtoResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/" + id).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(2)));
    }

    @Test
    @WithMockUser(username = "testUser")
    void depositToAccount() throws Exception {
        Account account = new Account();
        account.setNumber(UUID.randomUUID());
        account.setBalance(1000.0);

        AccountDtoResponse accountDtoResponse = new AccountDtoResponse();
        accountDtoResponse.setNumber(account.getNumber());
        accountDtoResponse.setBalance(account.getBalance());

        UUID accountNumber = UUID.randomUUID();
        double depositAmount = 100.0;

        when(accountService.depositToAccount(eq(accountNumber), eq(depositAmount)))
                .thenReturn(account);
        when(accountDtoMapperResponse.convertToDto(any(Account.class)))
                .thenReturn(accountDtoResponse);

        mockMvc.perform(post("/accounts/deposit/{number}", accountNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(depositAmount)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"number\":\"" + account.getNumber() + "\",\"balance\":" + account.getBalance() + "}"));
    }

    @Test
    @WithMockUser(username = "testUser")
    void withdrawFromAccount() throws Exception {
        Account account = new Account();
        account.setNumber(UUID.randomUUID());
        account.setBalance(1000.0);

        UUID accountNumber = account.getNumber();
        double withdrawAmount = 100.0;

        when(accountService.withdrawFromAccount(eq(accountNumber), eq(withdrawAmount)))
                .thenReturn(true);

        mockMvc.perform(put("/accounts/withdraw/{accountNumber}", accountNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(withdrawAmount)))
                .andExpect(status().isOk())
                .andExpect(content().string("Withdrawal successful"));
    }

    @Test
    @WithMockUser(username = "testUser")
    void transferMoney() throws Exception {
        Account from = new Account();
        from.setNumber(UUID.randomUUID());
        from.setBalance(2000.0);

        Account to = new Account();
        to.setNumber(UUID.randomUUID());
        to.setBalance(1000.0);

        UUID fromAccountNumber = from.getNumber();
        UUID toAccountNumber = to.getNumber();

        double moneyToTransfer = 100.0;

        doNothing().when(accountService).transferMoney(eq(fromAccountNumber), eq(toAccountNumber), eq(moneyToTransfer));

        mockMvc.perform(put("/accounts/transfer/{fromAccountNumber}/{toAccountNumber}", fromAccountNumber, toAccountNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(moneyToTransfer)))
                .andExpect(status().isOk())
                .andExpect(content().string("Transfer successful"));
    }
}
