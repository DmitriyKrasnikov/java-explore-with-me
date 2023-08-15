package ru.practicum.ewm.comment.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.model.CommentMapper;
import ru.practicum.ewm.comment.model.dto.CommentFullDto;
import ru.practicum.ewm.comment.model.dto.CommentShortDto;
import ru.practicum.ewm.comment.model.dto.NewCommentDto;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Transactional
    @Override
    public void adminDeleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException(String.format("Comment with id = %d not found", commentId)));

        commentRepository.delete(comment);
    }

    @Transactional
    @Override
    public void userDeleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException(String.format("Comment with id = %d not found", commentId)));

        if (!Objects.equals(comment.getUser().getId(), userId)) {
            throw new ConflictException(String.format("User id=%s is not creator comment id=%d", userId, commentId));
        }

        commentRepository.delete(comment);
    }

    @Transactional
    @Override
    public CommentFullDto createComment(Long eventId, Long userId, NewCommentDto newCommentDto) {
        return commentMapper.toFull(commentRepository.save(commentMapper.toComment(newCommentDto, eventId, userId)));

    }

    @Transactional
    @Override
    public CommentFullDto updateComment(Long commentId, Long userId, NewCommentDto newCommentDto) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException(String.format("Comment with id = %d not found", commentId)));
        commentMapper.updateComment(comment, userId, newCommentDto);
        return commentMapper.toFull(comment);
    }

    @Override
    public List<CommentShortDto> getEventsComment(Long eventId) {
        return commentRepository.findByEventId(eventId).stream().map(commentMapper::toShortDto).collect(Collectors.toList());
    }

    @Override
    public List<CommentFullDto> getUsersComment(Long eventId, Long userId) {
        return commentRepository.findByEventIdAndUserId(eventId, userId).stream().map(commentMapper::toFull).collect(Collectors.toList());
    }
}
