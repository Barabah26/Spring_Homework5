package com.example.spring_homework5.dao;

import com.example.spring_homework5.domain.Employer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployerJpaRepository extends JpaRepository<Employer, Long> {

}
