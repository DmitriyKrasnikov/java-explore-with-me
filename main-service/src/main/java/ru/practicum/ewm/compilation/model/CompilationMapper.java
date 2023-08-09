package ru.practicum.ewm.compilation.model;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.ewm.compilation.model.dto.CompilationDto;
import ru.practicum.ewm.compilation.model.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.model.dto.UpdateCompilationRequest;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventMapper;
import ru.practicum.ewm.event.repository.EventRepository;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {EventMapper.class})
public abstract class CompilationMapper {
    @Autowired
    private EventRepository eventRepository;

    @Mapping(source = "compilation.events", target = "events")
    public abstract CompilationDto toDto(Compilation compilation);

    protected List<Event> map(List<Long> value) {
        if (value == null) {
            return new ArrayList<>();
        }
        return eventRepository.findAllById(value);
    }

    public abstract Compilation toCompilation(NewCompilationDto newCompilationDto);

    public abstract void updateCompilationFromDto(UpdateCompilationRequest updateRequest, @MappingTarget Compilation compilation);

    @AfterMapping
    protected void afterUpdateCompilationFromDto(UpdateCompilationRequest updateRequest, @MappingTarget Compilation compilation) {
        if (updateRequest.getEvents() != null) {
            List<Event> events = eventRepository.findAllById(updateRequest.getEvents());
            compilation.setEvents(events);
        }
    }
}