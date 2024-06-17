package com.example.spring_homework5.mapper.account;

import com.example.spring_homework5.domain.Account;
import com.example.spring_homework5.domain.Customer;
import com.example.spring_homework5.dto.account.AccountDtoResponse;
import com.example.spring_homework5.mapper.DtoMapperFacade;
import org.springframework.stereotype.Service;

@Service
public class AccountDtoMapperResponse extends DtoMapperFacade<Account, AccountDtoResponse> {
    public AccountDtoMapperResponse() {
        super(Account.class, AccountDtoResponse.class);
    }

    protected void decorateDto(AccountDtoResponse dto, Account entity) {
        Customer customer = entity.getCustomer();
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found");
        }
        dto.setCustomerName(customer.getName());
    }
}
