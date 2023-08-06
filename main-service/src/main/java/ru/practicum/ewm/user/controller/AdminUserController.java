package ru.practicum.ewm.user.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.model.dto.NewUserRequest;
import ru.practicum.ewm.user.model.dto.UserDto;
import ru.practicum.ewm.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/admin/users")
@AllArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(name = "ids", required = false) List<Long> users,
                                  @RequestParam(defaultValue = "0") Integer from,
                                  @RequestParam(defaultValue = "10") Integer size) {
        List<UserDto> userDtoList;
        if (users != null) {
            userDtoList = userService.getUsers(users);
        } else {
            userDtoList = userService.getAllUsers(from, size);
        }

        return Objects.requireNonNullElseGet(userDtoList, ArrayList::new);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Validated NewUserRequest newUserRequest) {
        return userService.createUser(newUserRequest);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable(name = "userId") Long userId) {
        userService.deleteUser(userId);
    }
}
