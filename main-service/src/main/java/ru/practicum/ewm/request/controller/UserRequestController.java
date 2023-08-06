package ru.practicum.ewm.request.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.model.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.service.RequestService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/users/{userId}/requests")
@AllArgsConstructor
public class UserRequestController {

    private final RequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> getUsersRequest(@PathVariable(name = "userId") Long userId) {
        List<ParticipationRequestDto> participationRequests = requestService.getUsersRequest(userId);

        return Objects.requireNonNullElseGet(participationRequests, ArrayList::new);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createUserRequest(@PathVariable(name = "userId") Long userId,
                                                     @RequestParam(value = "eventId") Long eventId) {
        return requestService.createUserRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelUserRequest(@PathVariable(name = "userId") Long userId,
                                                     @PathVariable(name = "requestId") Long requestId) {
        return requestService.cancelUserRequest(userId, requestId);
    }
}
