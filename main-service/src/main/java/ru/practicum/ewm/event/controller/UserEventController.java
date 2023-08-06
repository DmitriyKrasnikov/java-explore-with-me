package ru.practicum.ewm.event.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.ewm.event.model.dto.EventFullDto;
import ru.practicum.ewm.event.model.dto.EventShortDto;
import ru.practicum.ewm.event.model.dto.NewEventDto;
import ru.practicum.ewm.event.model.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.request.model.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.model.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.model.dto.ParticipationRequestDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/users/{userId}/events")
@AllArgsConstructor
public class UserEventController {

    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getUsersEvents(@PathVariable(name = "userId") Long userId,
                                              @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @RequestParam(name = "size", defaultValue = "10") Integer size) {
        List<EventShortDto> events = eventService.getUsersEvents(userId, from, size);

        return Objects.requireNonNullElseGet(events, ArrayList::new);

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createUserEvent(@PathVariable(name = "userId") Long userId,
                                        @RequestBody @Validated NewEventDto newEventDto) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Field: eventDate");
        }

        return eventService.createUserEvent(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getUsersEvent(@PathVariable(name = "userId") Long userId,
                                      @PathVariable(name = "eventId") Long eventId) {
        return eventService.getUsersEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateUsersEvent(@PathVariable(name = "userId") Long userId,
                                         @PathVariable(name = "eventId") Long eventId,
                                         @RequestBody @Validated UpdateEventUserRequest updateEventUserRequest) {
        if (updateEventUserRequest.getEventDate() != null
                && updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Field: eventDate");
        }
        return eventService.updateUsersEvent(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getUsersEventRequests(@PathVariable(name = "userId") Long userId,
                                                               @PathVariable(name = "eventId") Long eventId) {
        List<ParticipationRequestDto> participationRequests = eventService.getUsersEventRequests(userId, eventId);

        return Objects.requireNonNullElseGet(participationRequests, ArrayList::new);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateUsersEventRequests(@PathVariable(name = "userId") Long userId,
                                                                   @PathVariable(name = "eventId") Long eventId,
                                                                   @RequestBody @Validated EventRequestStatusUpdateRequest request) {
        return eventService.updateUsersEventRequests(userId, eventId, request);
    }
}
