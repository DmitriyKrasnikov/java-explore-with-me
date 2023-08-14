package ru.practicum.ewm.request.service;

import ru.practicum.ewm.request.model.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getUsersRequest(Long userId);

    ParticipationRequestDto createUserRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelUserRequest(Long userId, Long requestId);
}
