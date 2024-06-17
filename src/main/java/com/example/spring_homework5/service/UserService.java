package com.example.spring_homework5.service;

import com.example.spring_homework5.dao.UserRepository;
import com.example.spring_homework5.domain.User;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Optional<User> getByLogin(@NonNull String login) {

        return userRepository.findUsersByUserName(login);
    }

}