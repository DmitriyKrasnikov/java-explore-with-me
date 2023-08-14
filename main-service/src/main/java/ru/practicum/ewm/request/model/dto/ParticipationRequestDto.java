package ru.practicum.ewm.request.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.request.model.RequestState;

import java.time.LocalDateTime;

import static ru.practicum.ewm.StatClient.DATE_TIME_FORMAT;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequestDto {
    private Long id;
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    private LocalDateTime created;
    private Long event;
    private Long requester;
    private RequestState status;
}
