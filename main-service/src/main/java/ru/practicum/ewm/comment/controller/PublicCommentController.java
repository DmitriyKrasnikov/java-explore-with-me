package ru.practicum.ewm.comment.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.comment.model.dto.CommentFullDto;
import ru.practicum.ewm.comment.model.dto.CommentShortDto;
import ru.practicum.ewm.comment.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/events/{eventId}/comments")
@AllArgsConstructor
public class PublicCommentController {

    private final CommentService commentService;

    @GetMapping
    public List<CommentShortDto> getEventsComment(@PathVariable(name = "eventId") Long eventId) {
        return commentService.getEventsComment(eventId);
    }

    @GetMapping("/users/{userId}")
    public List<CommentFullDto> getUsersComments(@PathVariable(name = "eventId") Long eventId,
                                                 @PathVariable(name = "userId") Long userId) {
        return commentService.getUsersComment(eventId, userId);
    }
}
