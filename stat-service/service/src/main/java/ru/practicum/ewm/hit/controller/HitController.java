package ru.practicum.ewm.hit.controller;

import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.ewm.hit.service.HitService;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.model.ViewStats;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
public class HitController {
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final HitService hitService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void createHit(@RequestBody EndpointHit endpointHit) {
        hitService.createHit(endpointHit);
    }

    @GetMapping("/stats")
    public List<ViewStats> getStats(@RequestParam(name = "start")
                                    @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime start,
                                    @RequestParam(name = "end")
                                    @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime end,
                                    @RequestParam(name = "uris", required = false) List<String> uris,
                                    @RequestParam(name = "unique", defaultValue = "false") Boolean unique) {
        if (start.isAfter(end)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Field: start after end");
        }
        if (uris == null) {
            uris = new ArrayList<>();
        }

        return hitService.getStats(start, end, uris, unique);
    }
}
