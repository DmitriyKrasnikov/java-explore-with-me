package ru.practicum.ewm.comment.service;

import ru.practicum.ewm.comment.model.dto.CommentFullDto;
import ru.practicum.ewm.comment.model.dto.CommentShortDto;
import ru.practicum.ewm.comment.model.dto.NewCommentDto;

import java.util.List;

public interface CommentService {
    void adminDeleteComment(Long commentId);

    CommentFullDto createComment(Long eventId, Long userId, NewCommentDto newCommentDto);

    CommentFullDto updateComment(Long commentId, Long userId, NewCommentDto newCommentDto);

    List<CommentShortDto> getEventsComment(Long eventId);

    List<CommentFullDto> getUsersComment(Long eventId, Long userId);
}
