package ru.practicum.ewm.user.model;

import org.mapstruct.Mapper;
import ru.practicum.ewm.user.model.dto.NewUserRequest;
import ru.practicum.ewm.user.model.dto.UserDto;
import ru.practicum.ewm.user.model.dto.UserShortDto;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserShortDto toShort(User user);

    UserDto toDto(User user);

    User toUser(NewUserRequest userRequest);

}
