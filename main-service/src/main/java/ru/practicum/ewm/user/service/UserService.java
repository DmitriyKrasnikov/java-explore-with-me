package ru.practicum.ewm.user.service;

import ru.practicum.ewm.user.model.dto.NewUserRequest;
import ru.practicum.ewm.user.model.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(List<Long> users);

    List<UserDto> getAllUsers(Integer from, Integer size);

    UserDto createUser(NewUserRequest newUserRequest);

    void deleteUser(Long userId);
}
