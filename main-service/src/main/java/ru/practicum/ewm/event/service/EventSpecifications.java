package ru.practicum.ewm.event.service;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.exception.ErrorHandler.DATE_TIME_FORMAT;

@Component
public class EventSpecifications {

    public Specification<Event> isPublished() {
        return (root, query, cb) -> cb.equal(root.get("state"), EventState.PUBLISHED);
    }

    public Specification<Event> hasUsers(List<Long> users) {
        return (root, query, cb) -> root.get("initiator").get("id").in(users);
    }

    public Specification<Event> hasStates(List<EventState> states) {
        return (root, query, cb) -> root.get("state").in(states);
    }

    public Specification<Event> hasCategories(List<Long> categories) {
        return (root, query, cb) -> root.get("category").get("id").in(categories);
    }

    public Specification<Event> hasRangeStart(String rangeStart) {
        LocalDateTime startDateTime = LocalDateTime.parse(rangeStart, DATE_TIME_FORMAT);
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("eventDate"), startDateTime);
    }

    public Specification<Event> hasRangeEnd(String rangeEnd) {
        LocalDateTime endDateTime = LocalDateTime.parse(rangeEnd, DATE_TIME_FORMAT);
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("eventDate"), endDateTime);
    }

    public Specification<Event> hasText(String text) {
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("annotation")), "%" + text.toLowerCase() + "%"),
                cb.like(cb.lower(root.get("description")), "%" + text.toLowerCase() + "%")
        );
    }

    public Specification<Event> isPaid(Boolean paid) {
        return (root, query, cb) -> cb.equal(root.get("paid"), paid);
    }

    public Specification<Event> isOnlyAvailable(boolean onlyAvailable) {
        return (root, query, cb) -> cb.greaterThan(root.get("participantLimit"), root.get("confirmedRequests"));
    }
}


