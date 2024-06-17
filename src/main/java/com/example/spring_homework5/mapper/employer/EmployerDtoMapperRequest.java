package com.example.spring_homework5.mapper.employer;

import com.example.spring_homework5.domain.Employer;
import com.example.spring_homework5.dto.employer.EmployerDtoRequest;
import com.example.spring_homework5.mapper.DtoMapperFacade;
import org.springframework.stereotype.Service;

@Service
public class EmployerDtoMapperRequest extends DtoMapperFacade<Employer, EmployerDtoRequest> {
    public EmployerDtoMapperRequest() {
        super(Employer.class, EmployerDtoRequest.class);
    }
}
