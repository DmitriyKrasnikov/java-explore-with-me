package ru.practicum.ewm.event.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.ewm.event.model.dto.EventFullDto;
import ru.practicum.ewm.event.model.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.service.EventService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/admin/events")
@AllArgsConstructor
public class AdminEventController {

    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getEvents(@RequestParam(value = "users", required = false) List<Long> users,
                                        @RequestParam(value = "states", required = false) List<String> states,
                                        @RequestParam(value = "categories", required = false) List<Long> categories,
                                        @RequestParam(value = "rangeStart", required = false) String rangeStart,
                                        @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
                                        @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                        @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {

        List<EventFullDto> eventFullDtoList = eventService
                .getEvents(users, states, categories, rangeStart, rangeEnd, from, size);

        return Objects.requireNonNullElseGet(eventFullDtoList, ArrayList::new);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvents(@PathVariable(name = "eventId") Long eventId,
                                     @RequestBody @Validated UpdateEventAdminRequest updateEventAdminRequest) {
        if (updateEventAdminRequest.getEventDate() != null &&
                updateEventAdminRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Field: eventDate");
        }

        return eventService.updateEvents(eventId, updateEventAdminRequest);
    }
}
