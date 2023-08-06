package ru.practicum.ewm.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.model.ParticipationRequest;
import ru.practicum.ewm.request.model.ParticipationRequestMapper;
import ru.practicum.ewm.request.model.RequestState;
import ru.practicum.ewm.request.model.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.repository.ParticipationRequestsRepository;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final ParticipationRequestsRepository requestsRepository;
    private final ParticipationRequestMapper requestMapper;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public List<ParticipationRequestDto> getUsersRequest(Long userId) {
        log.info("Get users request, user id = {}", userId);

        userIsExist(userId);
        return requestsRepository.findByRequesterId(userId).stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ParticipationRequestDto createUserRequest(Long userId, Long eventId) {
        log.info("Create user request, user id = {}, event id = {}", userId, eventId);

        userIsExist(userId);
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Event with id=%s was not found", eventId)));

        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ConflictException(String.format("The initiator %s cannot book his event %d ", userId, eventId));
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException(String.format("You cannot participate in an unpublished event %s", eventId));
        }

        if (event.getParticipantLimit() != 0 && event.getParticipantLimit()
                <= requestsRepository.countByEventIdAndStatus(eventId, RequestState.CONFIRMED)) {
            throw new ConflictException(String.format("The participation limit to event %s has been reached", eventId));
        }

        return requestMapper.toDto(requestsRepository.save(requestMapper.createParticipationRequest(userId, eventId)));
    }

    @Transactional
    @Override
    public ParticipationRequestDto cancelUserRequest(Long userId, Long requestId) {
        log.info("Cancel user request, user id = {}, request id = {}", userId, requestId);

        userIsExist(userId);
        ParticipationRequest participationRequest = requestsRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException(String.format("Request with id=%s not found", requestId)));

        if (!Objects.equals(participationRequest.getRequester().getId(), userId)) {
            throw new ConflictException(String
                    .format("You cannot cancel the request id=%s because you are not requester", requestId));
        }

        participationRequest.setStatus(RequestState.CANCELED);
        return requestMapper.toDto(participationRequest);
    }

    private void userIsExist(Long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id=%s was not found", userId)));
    }
}
