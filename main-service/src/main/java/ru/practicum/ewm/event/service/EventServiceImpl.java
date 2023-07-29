package ru.practicum.ewm.event.service;

import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.model.dto.EventFullDto;
import ru.practicum.ewm.event.model.dto.EventShortDto;
import ru.practicum.ewm.event.model.dto.NewEventDto;
import ru.practicum.ewm.event.model.dto.UpdateEventUserRequest;

import java.util.List;

@Service
public class EventServiceImpl implements EventService{
    @Override
    public List<EventShortDto> getUsersEvents(Long userId, Integer from, Integer size) {
        return null;
    }

    @Override
    public EventFullDto createUserEvent(Long userId, NewEventDto newEventDto) {
        return null;
    }

    @Override
    public EventFullDto getUsersEvent(Long userId, Long eventId) {
        return null;//if not found = not found
    }

    @Override
    public EventFullDto updateUsersEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {

        return null;//if state = published = validate exception and not found if not found
        //so statusami zamorochka
    }
}
