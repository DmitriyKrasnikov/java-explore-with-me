package ru.practicum.ewm.event.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.model.dto.EventFullDto;
import ru.practicum.ewm.event.model.dto.EventShortDto;
import ru.practicum.ewm.event.model.dto.NewEventDto;
import ru.practicum.ewm.event.model.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.exception.ValidateException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
        if (events == null) {
            return new ArrayList<>();
        }
        return events;
    }

    @PostMapping
    public EventFullDto createUserEvent(@PathVariable(name = "userId") Long userId,
                                        @RequestBody @Validated NewEventDto newEventDto) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidateException(String.format("Field: eventDate. Error: должно содержать дату," +
                    " которая еще не наступила. Value: %s", newEventDto.getEventDate().toString()));
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
                                         @RequestBody @Validated UpdateEventUserRequest updateEventUserRequest){
        if (updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidateException(String.format("Field: eventDate. Error: должно содержать дату," +
                    " которая еще не наступила. Value: %s", updateEventUserRequest.getEventDate().toString()));
        }
        return eventService.updateUsersEvent(userId, eventId, updateEventUserRequest);
    }
}
