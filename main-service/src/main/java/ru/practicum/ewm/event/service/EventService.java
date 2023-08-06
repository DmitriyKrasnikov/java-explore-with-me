package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.model.dto.*;
import ru.practicum.ewm.request.model.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.model.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.model.dto.ParticipationRequestDto;

import java.util.List;

public interface EventService {
    List<EventShortDto> getUsersEvents(Long userId, Integer from, Integer size);

    EventFullDto createUserEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getUsersEvent(Long userId, Long eventId);

    EventFullDto updateUsersEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<ParticipationRequestDto> getUsersEventRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateUsersEventRequests(Long userId, Long eventId, EventRequestStatusUpdateRequest request);

    List<EventFullDto> getEvents(
            List<Long> users,
            List<String> states,
            List<Long> categories,
            String rangeStart,
            String rangeEnd,
            Integer from,
            Integer size
    );

    EventFullDto updateEvents(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventShortDto> getPublicEvents(
            String text,
            List<Long> categories,
            Boolean paid,
            String rangeStart,
            String rangeEnd,
            boolean onlyAvailable,
            String sort,
            int from,
            int size);

    EventFullDto getPublicEvent(Long id);
}
