package ru.practicum.ewm.event.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.model.dto.EventFullDto;
import ru.practicum.ewm.event.model.dto.EventShortDto;
import ru.practicum.ewm.event.model.dto.NewEventDto;
import ru.practicum.ewm.event.model.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.request.model.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.model.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.model.dto.ParticipationRequestDto;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@AllArgsConstructor
public class UserEventController {

    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getUsersEvents(@PathVariable(name = "userId") Long userId,
                                              @RequestParam(name = "from", defaultValue = "0") @Min(0) Integer from,
                                              @RequestParam(name = "size", defaultValue = "10") @Min(0) Integer size) {
        return eventService.getUsersEvents(userId, from, size);

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createUserEvent(@PathVariable(name = "userId") Long userId,
                                        @RequestBody @Validated NewEventDto newEventDto) {
        return eventService.createUserEvent(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getUsersEvent(@PathVariable(name = "userId") Long userId,
                                      @PathVariable(name = "eventId") Long eventId) {
        return eventService.getUsersEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateUsersEvent(@PathVariable(name = "userId") Long userId,
                                         @PathVariable(name = "eventId") Long eventId,
                                         @RequestBody @Validated UpdateEventUserRequest updateEventUserRequest) {
        return eventService.updateUsersEvent(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getUsersEventRequests(@PathVariable(name = "userId") Long userId,
                                                               @PathVariable(name = "eventId") Long eventId) {
        return eventService.getUsersEventRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateUsersEventRequests(@PathVariable(name = "userId") Long userId,
                                                                   @PathVariable(name = "eventId") Long eventId,
                                                                   @RequestBody @Validated EventRequestStatusUpdateRequest request) {
        return eventService.updateUsersEventRequests(userId, eventId, request);
    }
}
