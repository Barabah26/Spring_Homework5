package com.example.spring_homework4.dao;

import com.example.spring_homework5.dao.EmployerJpaRepository;
import com.example.spring_homework5.domain.Employer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EmployerJpaRepositoryTest {

    @Autowired
    private EmployerJpaRepository employerJpaRepository;

    @Test
    void testSaveEmployer() {
        Employer employer = new Employer();
        employer.setName("Test Employer");
        employer.setAddress("Test address");
        Employer savedEmployer = employerJpaRepository.save(employer);
        assertThat(savedEmployer).isNotNull();
        assertThat(savedEmployer.getId()).isNotNull();
        assertThat(savedEmployer.getName()).isEqualTo("Test Employer");
    }

    @Test
    void testFindById() {
        Employer employer = new Employer();
        employer.setName("Test Employer");
        employer.setAddress("Test address");
        Employer savedEmployer = employerJpaRepository.save(employer);
        Optional<Employer> foundEmployer = employerJpaRepository.findById(savedEmployer.getId());
        assertThat(foundEmployer).isPresent();
        assertThat(foundEmployer.get().getName()).isEqualTo("Test Employer");
    }

}