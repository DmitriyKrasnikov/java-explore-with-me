package ru.practicum.ewm.request.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.model.dto.ParticipationRequestDto;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public abstract class ParticipationRequestMapper {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;

    @Mapping(source = "participationRequest.event.id", target = "event")
    @Mapping(source = "participationRequest.requester.id", target = "requester")
    public abstract ParticipationRequestDto toDto(ParticipationRequest participationRequest);

    public ParticipationRequest createParticipationRequest(Long userId, Long eventId) {
        User user = getUser(userId);
        Event event = getEvent(eventId);

        ParticipationRequest request = new ParticipationRequest();
        request.setCreated(LocalDateTime.now());
        request.setEvent(event);
        request.setRequester(user);
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            request.setStatus(RequestState.CONFIRMED);
        } else {
            request.setStatus(RequestState.PENDING);
        }

        return request;
    }

    protected Event getEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id = %s not found", eventId)));
    }

    protected User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id = %s not found", userId)));
    }
}
