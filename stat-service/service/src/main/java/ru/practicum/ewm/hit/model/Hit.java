package ru.practicum.ewm.hit.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

import static ru.practicum.ewm.hit.controller.HitController.DATE_TIME_FORMAT;

@Data
@NoArgsConstructor
@Entity
@Table(name = "hit")
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "app")
    private String app;
    @Column(name = "uri")
    private String uri;
    @Column(name = "ip")
    private String ip;
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    @Column(name = "timestamp")
    private LocalDateTime timestamp;
}
