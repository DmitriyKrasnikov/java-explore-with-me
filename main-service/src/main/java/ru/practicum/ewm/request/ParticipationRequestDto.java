package ru.practicum.ewm.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.model.EventState;

import javax.persistence.*;
import java.time.LocalDateTime;

import static ru.practicum.ewm.StatClient.DATE_TIME_FORMAT;

@Data
@Entity
@Table(name = "participation_requests")
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequestDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    @Column(name = "created")
    private LocalDateTime created;
    @Column(name = "event_id")
    private Long event;
    @Column(name = "requester_id")
    private Long requester;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EventState status;// Может завести отдельный статус?
}
