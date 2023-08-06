package ru.practicum.ewm.event.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.ewm.StatClient;
import ru.practicum.ewm.event.model.dto.EventFullDto;
import ru.practicum.ewm.event.model.dto.EventShortDto;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.exception.ConflictException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.exception.ErrorHandler.DATE_TIME_FORMAT;

@RestController
@RequestMapping("/events")
@AllArgsConstructor
@Slf4j
public class PublicEventController {

    private final EventService eventService;
    private final StatClient statClient;
    private final String app = "explore with me";


    @GetMapping
    public List<EventShortDto> getPublicEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(required = false, defaultValue = "false") boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        if (rangeStart != null && rangeEnd != null) {
            LocalDateTime startDateTime = LocalDateTime.parse(rangeStart, DATE_TIME_FORMAT);
            LocalDateTime endDateTime = LocalDateTime.parse(rangeEnd, DATE_TIME_FORMAT);
            if (startDateTime.isAfter(endDateTime)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "rangeStart must be before rangeEnd");
            }
        }
        List<EventShortDto> eventShortDtoList = eventService.getPublicEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        ResponseEntity<Object> statResponse = statClient.postStatistic(app, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
        if (!statResponse.getStatusCode().is2xxSuccessful()) {
            log.error("Error sending statistics: {}", statResponse.getStatusCode());
            throw new ConflictException("Error sending statistics");
        }

        return eventShortDtoList;
    }

    @GetMapping("/{id}")
    public EventFullDto getPublicEvent(@PathVariable Long id, HttpServletRequest request) {
        EventFullDto eventFullDto = eventService.getPublicEvent(id);

        ResponseEntity<Object> statResponse = statClient.postStatistic(app, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
        if (!statResponse.getStatusCode().is2xxSuccessful()) {
            log.error("Error sending statistics: {}", statResponse.getStatusCode());
            throw new ConflictException("Error sending statistics");
        }

        return eventFullDto;
    }
}
