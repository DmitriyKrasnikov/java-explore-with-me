package ru.practicum.ewm.request.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

import static ru.practicum.ewm.StatClient.DATE_TIME_FORMAT;

@Data
@Entity
@Table(name = "participation_requests")
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    @Column(name = "created")
    private LocalDateTime created;
    @JoinColumn(name = "event_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Event event;
    @JoinColumn(name = "requester_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User requester;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private RequestState status;
}
