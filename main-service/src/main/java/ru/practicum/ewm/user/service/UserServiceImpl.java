package ru.practicum.ewm.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.model.UserMapper;
import ru.practicum.ewm.user.model.dto.NewUserRequest;
import ru.practicum.ewm.user.model.dto.UserDto;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getUsers(List<Long> users) {
        log.info("Get users = {}", users);

        return userRepository.findByIdIn(users).stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<UserDto> getAllUsers(Integer from, Integer size) {
        log.info("Get all users, from = {}, size = {}", from, size);

        Pageable page = PageRequest.of(from / size, size);
        return userRepository.findAll(page).stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserDto createUser(NewUserRequest newUserRequest) {
        log.info("Create user, email = {}", newUserRequest.getEmail());

        return userMapper.toDto(userRepository.save(userMapper.toUser(newUserRequest)));
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        log.info("Delete user = {}", userId);

        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id = %s not found", userId)));

        userRepository.delete(user);
    }
}
