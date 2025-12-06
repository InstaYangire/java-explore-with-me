package ru.practicum.ewm.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.dto.NewUserDto;
import ru.practicum.ewm.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(NewUserDto dto);

    void deleteUser(Long userId);

    List<UserDto> getUsers(List<Long> ids, Pageable pageable);
}
