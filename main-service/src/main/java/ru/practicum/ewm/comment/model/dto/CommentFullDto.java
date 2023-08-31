package ru.practicum.ewm.comment.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static ru.practicum.ewm.StatClient.DATE_TIME_FORMAT;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentFullDto {
    private Long id;
    private String content;
    private Long userId;
    private Long eventId;
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    private LocalDateTime createdOn;
}
