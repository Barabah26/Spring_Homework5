package com.example.spring_homework5.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CustomerDtoResponse {
    private Long id;
    private String name;
    private String email;
    private Integer age;
    private Set<UUID> accountNumbers;
    private List<String> employerNames;
    private String phoneNumber;
    private LocalDateTime creationDate;
    private LocalDateTime lastModifiedDate;
}
