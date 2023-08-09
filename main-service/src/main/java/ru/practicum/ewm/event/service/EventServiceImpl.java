package ru.practicum.ewm.event.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventMapper;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.dto.*;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.pagination.FromSizePage;
import ru.practicum.ewm.request.model.ParticipationRequest;
import ru.practicum.ewm.request.model.ParticipationRequestMapper;
import ru.practicum.ewm.request.model.RequestState;
import ru.practicum.ewm.request.model.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.model.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.model.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.repository.ParticipationRequestsRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.exception.ErrorHandler.DATE_TIME_FORMAT;

@Service
@Slf4j
@AllArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventSpecifications eventSpecifications;
    private final ParticipationRequestsRepository requestsRepository;
    private final ParticipationRequestMapper requestMapper;

    @Override
    public List<EventShortDto> getUsersEvents(Long userId, Integer from, Integer size) {
        log.info("Get users events, user id = {}, from = {}, size = {}", userId, from, size);

        Pageable page = FromSizePage.ofFromSize(from, size);
        return eventRepository.findByInitiatorId(userId, page).stream().map(eventMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventFullDto createUserEvent(Long userId, NewEventDto newEventDto) {
        log.info("Create users events, user id = {}, event annotation = {}", userId, newEventDto.getAnnotation());

        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Field: eventDate");
        }

        Event event = eventMapper.toEvent(newEventDto, userId);
        eventRepository.save(event);
        return eventMapper.toFull(event);
    }

    @Override
    public EventFullDto getUsersEvent(Long userId, Long eventId) {
        log.info("Get users event, user id = {}, event id = {}", userId, eventId);

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                new NotFoundException(String.format("Event with id=%s was not found", eventId)));
        return eventMapper.toFull(event);
    }

    @Transactional
    @Override
    public EventFullDto updateUsersEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        log.info("Update users event, user id = {}, event id = {}, update event = {}", userId, eventId, updateEventUserRequest);

        if (updateEventUserRequest.getEventDate() != null
                && updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Field: eventDate");
        }

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                new NotFoundException(String.format("Event with id=%s was not found", eventId)));

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        eventMapper.updateEventFromUserRequestDto(updateEventUserRequest, event);

        return eventMapper.toFull(event);
    }

    @Override
    public List<ParticipationRequestDto> getUsersEventRequests(Long userId, Long eventId) {
        log.info("Get users event requests, user id = {}, event id = {}", userId, eventId);

        return requestsRepository.findByEventId(eventId).stream().map(requestMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateUsersEventRequests(Long userId, Long eventId, EventRequestStatusUpdateRequest request) {
        log.info("Update users event requests, user id = {}, event id = {}", userId, eventId);

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                new NotFoundException(String.format("Event with id=%s was not found", eventId)));

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();

        List<ParticipationRequest> pendingRequests = requestsRepository
                .findByIdInAndEventIdAndStatusOrderByCreatedAsc(request.getRequestIds(), eventId, RequestState.PENDING);

        if (event.getParticipantLimit() == 0) {
            pendingRequests.forEach(r -> r.setStatus(RequestState.CONFIRMED));
            result.setConfirmedRequests(pendingRequests.stream()
                    .map(requestMapper::toDto)
                    .collect(Collectors.toList()));
            result.setRejectedRequests(new ArrayList<>());
        } else {
            long confirmRequestCount = requestsRepository.countByEventIdAndStatus(eventId, RequestState.CONFIRMED);
            long eventParticipantLimit = event.getParticipantLimit();
            long freeCountToLimit = eventParticipantLimit - confirmRequestCount;

            if (freeCountToLimit <= 0) {
                throw new ConflictException("The participant limit has been reached");
            }

            if (request.getStatus().equals(EventRequestStatus.CONFIRMED)) {
                List<ParticipationRequest> confirmedRequests = pendingRequests.stream()
                        .limit(freeCountToLimit)
                        .collect(Collectors.toList());
                confirmedRequests.forEach(r -> r.setStatus(RequestState.CONFIRMED));

                result.setConfirmedRequests(confirmedRequests.stream()
                        .map(requestMapper::toDto)
                        .collect(Collectors.toList()));

                List<ParticipationRequest> rejectedRequests = pendingRequests.stream()
                        .skip(freeCountToLimit)
                        .collect(Collectors.toList());
                rejectedRequests.forEach(r -> r.setStatus(RequestState.REJECTED));

                result.setRejectedRequests(rejectedRequests.stream()
                        .map(requestMapper::toDto)
                        .collect(Collectors.toList()));
            } else {
                pendingRequests.forEach(r -> r.setStatus(RequestState.REJECTED));
                result.setRejectedRequests(pendingRequests.stream()
                        .map(requestMapper::toDto)
                        .collect(Collectors.toList()));

                result.setConfirmedRequests(new ArrayList<>());
            }
        }

        return result;
    }

    @Override
    public List<EventFullDto> getEvents(
            List<Long> users,
            List<String> states,
            List<Long> categories,
            String rangeStart,
            String rangeEnd,
            Integer from,
            Integer size
    ) {
        log.info("Get events, users = {}, states = {}, categories = {}, rangeStart = {}, rangeEnd = {}, from = {}, size = {}",
                users, states, categories, rangeStart, rangeEnd, from, size);

        Specification<Event> spec = Specification.where(null);
        if (users != null && !users.isEmpty()) {
            spec = spec.and(eventSpecifications.hasUsers(users));
        }

        if (states != null && !states.isEmpty()) {
            List<EventState> eventStates = states.stream()
                    .map(EventState::valueOf)
                    .collect(Collectors.toList());
            spec = spec.and(eventSpecifications.hasStates(eventStates));
        }

        if (categories != null && !categories.isEmpty()) {
            spec = spec.and(eventSpecifications.hasCategories(categories));
        }

        if (rangeStart != null) {
            spec = spec.and(eventSpecifications.hasRangeStart(rangeStart));
        }

        if (rangeEnd != null) {
            spec = spec.and(eventSpecifications.hasRangeEnd(rangeEnd));
        }

        Pageable pageable = PageRequest.of(from, size);

        Page<Event> eventsPage = eventRepository.findAll(spec, pageable);

        return eventsPage.getContent().stream()
                .map(eventMapper::toFull)
                .collect(Collectors.toList());
    }


    @Transactional
    @Override
    public EventFullDto updateEvents(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("Update events, event id = {}, admin request = {}", eventId, updateEventAdminRequest);

        if (updateEventAdminRequest.getEventDate() != null &&
                updateEventAdminRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Field: eventDate");
        }

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Event with id=%s was not found", eventId)));

        if (updateEventAdminRequest.getStateAction() != null) {
            if (!event.getState().equals(EventState.PENDING)) {
                throw new ConflictException(String
                        .format("Cannot publish the event because it's not in the right state: %s", event.getState()));
            }
        }

        eventMapper.updateEventFromAdminRequestDto(updateEventAdminRequest, event);
        return eventMapper.toFull(event);
    }

    @Override
    public List<EventShortDto> getPublicEvents(
            String text,
            List<Long> categories,
            Boolean paid,
            String rangeStart,
            String rangeEnd,
            boolean onlyAvailable,
            String sort,
            int from,
            int size
    ) {
        log.info("Get public events, text = {}, categories = {}, paid = {}, rangeStart = {}, rangeEnd = {}," +
                        " onlyAvailable = {}, sort = {}, from = {}, size = {}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        if (rangeStart != null && rangeEnd != null) {
            LocalDateTime startDateTime = LocalDateTime.parse(rangeStart, DATE_TIME_FORMAT);
            LocalDateTime endDateTime = LocalDateTime.parse(rangeEnd, DATE_TIME_FORMAT);
            if (startDateTime.isAfter(endDateTime)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "rangeStart must be before rangeEnd");
            }
        }

        Specification<Event> spec = Specification.where(null);

        if (text != null && !text.isEmpty()) {
            spec = spec.and(eventSpecifications.hasText(text));
        }

        if (categories != null && !categories.isEmpty()) {
            spec = spec.and(eventSpecifications.hasCategories(categories));
        }

        if (paid != null) {
            spec = spec.and(eventSpecifications.isPaid(paid));
        }

        if (rangeStart != null) {
            spec = spec.and(eventSpecifications.hasRangeStart(rangeStart));
        } else {
            LocalDateTime now = LocalDateTime.now();
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("eventDate"), now));
        }

        if (rangeEnd != null) {
            spec = spec.and(eventSpecifications.hasRangeEnd(rangeEnd));
        }

        if (onlyAvailable) {
            spec = spec.and(eventSpecifications.isOnlyAvailable(onlyAvailable));
        }

        Pageable pageable = PageRequest.of(from, size);

        if (sort != null && sort.equals("EVENT_DATE")) {
            pageable = PageRequest.of(from, size, Sort.by("eventDate"));
        } else if (sort != null && sort.equals("VIEWS")) {
            pageable = PageRequest.of(from, size, Sort.by("views"));
        }

        Page<Event> eventsPage = eventRepository.findAll(spec.and(eventSpecifications.isPublished()), pageable);

        return eventsPage.getContent().stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getPublicEvent(Long id) {
        log.info("Get public event id = {}", id);

        Event event = eventRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Event with id=%s was not found", id)));

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException(String.format("Event with id=%s was not found", id));
        }

        return eventMapper.toFull(event);
    }
}
