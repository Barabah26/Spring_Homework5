package com.example.spring_homework4.service;

import com.example.spring_homework5.dao.EmployerJpaRepository;
import com.example.spring_homework5.domain.Employer;
import com.example.spring_homework5.service.DefaultEmployerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultEmployerServiceTest {

    @Mock
    private EmployerJpaRepository employerJpaRepository;

    @InjectMocks
    private DefaultEmployerService employerService;

    @Captor
    private ArgumentCaptor<Long> employerArgumentCaptor;

    @Test
    void save() {
        Employer testEmployer = new Employer();
        testEmployer.setName("Tech Group");

        when(employerJpaRepository.save(testEmployer)).thenReturn(testEmployer);

        Employer savedEmployer = employerService.save(testEmployer);
        assertEquals(testEmployer, savedEmployer, "The saved employer should match the test employer");
        verify(employerJpaRepository).save(testEmployer);
    }

    @Test
    void delete() {
        Employer testEmployer = new Employer();
        testEmployer.setName("Tech Corp");
        employerService.delete(testEmployer);
        verify(employerJpaRepository).delete(testEmployer);
    }

    @Test
    void deleteAll() {
        employerService.deleteAll();
        verify(employerJpaRepository).deleteAll();
    }

    @Test
    void saveAll() {
        Employer employer1 = new Employer();
        employer1.setName("Tech Corp");
        Employer employer2 = new Employer();
        employer2.setName("Innovate Ltd");

        employerService.saveAll(employer1);
        employerService.saveAll(employer2);

        verify(employerJpaRepository).save(employer1);
        verify(employerJpaRepository).save(employer2);
    }

    @Test
    void findAll() {
        Employer employer1 = new Employer();
        Employer employer2 = new Employer();
        List<Employer> employersExpected = List.of(employer1, employer2);
        when(employerJpaRepository.findAll()).thenReturn(employersExpected);

        List<Employer> employeesActual = employerService.findAll();
        assertNotNull(employeesActual);
        assertFalse(employeesActual.isEmpty());
        assertIterableEquals(employersExpected, employeesActual);
    }


    @Test
    void deleteById() {
        Long testId = 1L;
        employerService.deleteById(testId);
        verify(employerJpaRepository).deleteById(employerArgumentCaptor.capture());
        assertEquals(testId, employerArgumentCaptor .getValue(), "Captured ID should match the test ID");
    }

    @Test
    void getOne() {
        Employer testEmployer = new Employer();
        String lastName = "Tech Corp";
        testEmployer.setName(lastName);
        when(employerJpaRepository.findById(1L)).thenReturn(Optional.of(testEmployer));
        Optional<Employer> optionalEmployer = employerService.getOne(1L);
        assertTrue(optionalEmployer.isPresent(), "Employer should be present");
        Employer employer = optionalEmployer.get();
        assertEquals(testEmployer.getName(), employer.getName(), "Employer names should match");
    }

}