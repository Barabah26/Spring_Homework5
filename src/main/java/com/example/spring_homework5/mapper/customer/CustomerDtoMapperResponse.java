package com.example.spring_homework5.mapper.customer;

import com.example.spring_homework5.domain.Account;
import com.example.spring_homework5.domain.Customer;
import com.example.spring_homework5.domain.Employer;
import com.example.spring_homework5.dto.customer.CustomerDtoResponse;
import com.example.spring_homework5.mapper.DtoMapperFacade;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomerDtoMapperResponse extends DtoMapperFacade<Customer, CustomerDtoResponse> {
    public CustomerDtoMapperResponse() {
        super(Customer.class, CustomerDtoResponse.class);
    }

    @Override
    protected void decorateDto(CustomerDtoResponse dto, Customer entity) {
        if (entity.getAccounts() != null) {
            Set<UUID> accountNumbers = new HashSet<>();
            for (Account account : entity.getAccounts()) {
                UUID number = account.getNumber();
                accountNumbers.add(number);
            }
            dto.setAccountNumbers(accountNumbers);
        }

        if (entity.getEmployers() != null) {
            List<String> employerNames = new ArrayList<>();
            for (Employer employer : entity.getEmployers()) {
                String name = employer.getName();
                employerNames.add(name);
            }
            dto.setEmployerNames(employerNames);
        }
    }
}
