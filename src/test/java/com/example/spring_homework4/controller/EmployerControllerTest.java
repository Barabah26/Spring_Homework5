package com.example.spring_homework4.controller;

import com.example.spring_homework5.domain.Employer;
import com.example.spring_homework5.dto.employer.EmployerDtoResponse;
import com.example.spring_homework5.mapper.employer.EmployerDtoMapperResponse;
import com.example.spring_homework5.service.DefaultEmployerService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class EmployerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DefaultEmployerService employerService;

    @MockBean
    private EmployerDtoMapperResponse employerDtoMapperResponse;
    @Test
    @WithMockUser(username = "testUser")
    void getAll() throws Exception {
        Employer employer = new Employer();

        EmployerDtoResponse employerDtoResponse = new EmployerDtoResponse();
        employerDtoResponse.setName("Tech Corp");
        employerDtoResponse.setAddress("123 Tech Road");

        when(employerService.findAll()).thenReturn(List.of(employer));
        when(employerDtoMapperResponse.convertToDto(employer)).thenReturn(employerDtoResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/employer").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is("Tech Corp")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].address", Matchers.is("123 Tech Road")));
    }
}