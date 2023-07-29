package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.model.dto.EventFullDto;
import ru.practicum.ewm.event.model.dto.EventShortDto;
import ru.practicum.ewm.event.model.dto.NewEventDto;
import ru.practicum.ewm.event.model.dto.UpdateEventUserRequest;

import java.util.List;

public interface EventService {
    List<EventShortDto> getUsersEvents(Long userId, Integer from, Integer size);

    EventFullDto createUserEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getUsersEvent(Long userId, Long eventId);

    EventFullDto updateUsersEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);
}
