package ru.practicum.ewm.comment.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.ewm.comment.model.dto.CommentFullDto;
import ru.practicum.ewm.comment.model.dto.CommentShortDto;
import ru.practicum.ewm.comment.model.dto.NewCommentDto;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import javax.persistence.EntityNotFoundException;

@Mapper(componentModel = "spring")
public abstract class CommentMapper {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Mapping(target = "event", expression = "java(findEventById(eventId))")
    @Mapping(target = "user", expression = "java(findUserById(userId))")
    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")
    public abstract Comment toComment(NewCommentDto newCommentDto, Long eventId, Long userId);

    protected Event findEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));
    }

    protected User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "eventId", source = "event.id")
    public abstract CommentFullDto toFull(Comment comment);

    public void updateComment(Comment comment, Long userId, NewCommentDto newCommentDto) {
        if (!comment.getUser().getId().equals(userId)) {
            throw new ConflictException("User is not the creator of the comment");
        }
        updateCommentFromDto(comment, newCommentDto);
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    public abstract void updateCommentFromDto(@MappingTarget Comment comment, NewCommentDto newCommentDto);

    @Mapping(target = "userId", source = "user.id")
    public abstract CommentShortDto toShortDto(Comment comment);
}