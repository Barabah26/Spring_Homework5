package com.example.spring_homework5.mapper.customer;

import com.example.spring_homework5.domain.Customer;
import com.example.spring_homework5.dto.customer.CustomerDtoRequest;
import com.example.spring_homework5.mapper.DtoMapperFacade;
import org.springframework.stereotype.Service;

@Service
public class CustomerDtoMapperRequest extends DtoMapperFacade<Customer, CustomerDtoRequest> {

    public CustomerDtoMapperRequest() {
        super(Customer.class, CustomerDtoRequest.class);
    }
}
