package ru.practicum.ewm.comment.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.model.dto.CommentFullDto;
import ru.practicum.ewm.comment.model.dto.NewCommentDto;
import ru.practicum.ewm.comment.service.CommentService;

@RestController
@RequestMapping("/users/{userId}/comments")
@AllArgsConstructor
public class UserCommentController {

    private final CommentService commentService;

    @PostMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentFullDto createComment(@PathVariable(name = "eventId") Long eventId,
                                        @PathVariable(name = "userId") Long userId,
                                        @RequestBody @Validated NewCommentDto newCommentDto) {
        return commentService.createComment(eventId, userId, newCommentDto);
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentFullDto updateComment(@PathVariable(name = "commentId") Long commentId,
                                        @PathVariable(name = "userId") Long userId,
                                        @RequestBody @Validated NewCommentDto newCommentDto) {
        return commentService.updateComment(commentId, userId, newCommentDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long commentId) {
        commentService.adminDeleteComment(commentId);
    }
}
