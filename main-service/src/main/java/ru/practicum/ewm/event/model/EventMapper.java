package ru.practicum.ewm.event.model;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.ewm.StatClient;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.model.CategoryMapper;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.comment.model.CommentMapper;
import ru.practicum.ewm.event.model.dto.*;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.model.RequestState;
import ru.practicum.ewm.request.repository.ParticipationRequestsRepository;
import ru.practicum.ewm.user.model.UserMapper;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {CategoryMapper.class, UserMapper.class, CommentMapper.class})
public abstract class EventMapper {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ParticipationRequestsRepository requestsRepository;
    @Autowired
    private StatClient statClient;

    @Mapping(source = "event.category", target = "category")
    @Mapping(source = "event.initiator", target = "initiator")
    @Mapping(target = "confirmedRequests", expression = "java(getConfirmedRequests(event))")
    @Mapping(target = "views", expression = "java(getViews(event))")
    @Mapping(target = "comments", source = "event.comments")
    public abstract EventFullDto toFull(Event event);

    @Mapping(target = "confirmedRequests", expression = "java(getConfirmedRequests(event))")
    @Mapping(target = "views", expression = "java(getViews(event))")
    public abstract EventShortDto toDto(Event event);

    protected Integer getConfirmedRequests(Event event) {
        return requestsRepository.countByEventIdAndStatus(event.getId(), RequestState.CONFIRMED);
    }

    protected Long getViews(Event event) {
        if (event.getPublishedOn() == null) {
            return 0L;
        }

        String uri = "/events/" + event.getId();
        LocalDateTime start = event.getPublishedOn();
        LocalDateTime end = LocalDateTime.now();
        List<String> uris = Collections.singletonList(uri);
        Boolean unique = true;

        ResponseEntity<List<Map<String, Object>>> response = statClient.getStatistic(start, end, uris, unique);
        if (response.getStatusCode() == HttpStatus.OK) {
            List<Map<String, Object>> responseBody = response.getBody();
            if (responseBody != null && !responseBody.isEmpty()) {
                Map<String, Object> viewStats = responseBody.get(0);
                return ((Number) viewStats.get("hits")).longValue();
            }
        }
        return 0L;
    }

    protected Category map(Integer value) {
        if (value == null) {
            return null;
        }
        return categoryRepository.findById(value).orElse(null);
    }

    public abstract Event toEvent(NewEventDto newEventDto, Long userId);

    @AfterMapping
    protected void afterMapping(NewEventDto newEventDto, @MappingTarget Event event, Long userId) {
        if (userId != null) {
            event.setInitiator(userRepository.findById(userId).orElseThrow(() ->
                    new NotFoundException(String.format("User with id = %s not found", userId))));
        }
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);
        event.setComments(new ArrayList<>());
    }

    public abstract void updateEventFromUserRequestDto(UpdateEventUserRequest updateEventUserRequest, @MappingTarget Event event);

    @AfterMapping
    protected void afterMapping(UpdateEventUserRequest updateEventUserRequest, @MappingTarget Event event) {
        if (updateEventUserRequest.getCategory() != null) {
            event.setCategory(categoryRepository.findById(updateEventUserRequest.getCategory()).orElseThrow(() ->
                    new NotFoundException(String.format("Category with id = %s not found", updateEventUserRequest.getCategory()))));
        }
        if (updateEventUserRequest.getStateAction() != null) {
            switch (updateEventUserRequest.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
            }
        }
    }

    public abstract void updateEventFromAdminRequestDto(UpdateEventAdminRequest updateEventAdminRequest, @MappingTarget Event event);

    @AfterMapping
    protected void afterMapping(UpdateEventAdminRequest updateEventAdminRequest, @MappingTarget Event event) {
        if (updateEventAdminRequest.getCategory() != null) {
            event.setCategory(categoryRepository.findById(updateEventAdminRequest.getCategory()).orElseThrow(() ->
                    new NotFoundException(String.format("Category with id = %s not found", updateEventAdminRequest.getCategory()))));
        }
        if (updateEventAdminRequest.getStateAction() != null) {
            switch (updateEventAdminRequest.getStateAction()) {
                case PUBLISH_EVENT:
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    event.setState(EventState.CANCELED);
                    break;
            }
        }
    }
}
