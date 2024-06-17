package com.example.spring_homework5.mapper.user;

import com.example.spring_homework5.domain.User;
import com.example.spring_homework5.dto.user.UserDto;
import com.example.spring_homework5.mapper.DtoMapperFacade;
import org.springframework.stereotype.Service;

@Service
public class UserDtoMapper extends DtoMapperFacade<User, UserDto> {

    public UserDtoMapper() {
        super(User.class, UserDto.class);
    }

    @Override
    protected void decorateDto(UserDto dto, User user) {
        dto.setUserName(user.getUserName());
        dto.setSysRoles(String.valueOf(user.getRoles()));
    }
}
