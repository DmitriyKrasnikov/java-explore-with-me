package ru.practicum.ewm.event.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.StatClient;
import ru.practicum.ewm.event.model.dto.EventFullDto;
import ru.practicum.ewm.event.model.dto.EventShortDto;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.exception.ConflictException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@AllArgsConstructor
@Slf4j
public class PublicEventController {

    private final static String APP = "explore with me";
    private final EventService eventService;
    private final StatClient statClient;


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getPublicEvents(
            @RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "categories", required = false) List<Long> categories,
            @RequestParam(value = "paid", required = false) Boolean paid,
            @RequestParam(value = "rangeStart", required = false) String rangeStart,
            @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
            @RequestParam(value = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(0) Integer size,
            HttpServletRequest request
    ) {
        ResponseEntity<Object> statResponse = statClient
                .postStatistic(APP, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());

        if (!statResponse.getStatusCode().is2xxSuccessful()) {
            log.error("Error sending statistics: {}", statResponse.getStatusCode());
            throw new ConflictException("Error sending statistics");
        }

        return eventService
                .getPublicEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getPublicEvent(@PathVariable Long id, HttpServletRequest request) {
        ResponseEntity<Object> statResponse = statClient
                .postStatistic(APP, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());

        if (!statResponse.getStatusCode().is2xxSuccessful()) {
            log.error("Error sending statistics: {}", statResponse.getStatusCode());
            throw new ConflictException("Error sending statistics");
        }

        return eventService.getPublicEvent(id);
    }
}
