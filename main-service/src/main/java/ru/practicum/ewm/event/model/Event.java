package ru.practicum.ewm.event.model;

import jdk.jfr.BooleanFlag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "events")
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Size(min = 20, max = 2000)
    @Column(name = "annotation")
    private String annotation;
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;
    @NotNull
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    @NotBlank
    @Size(min = 20, max = 7000)
    @Column(name = "description")
    private String description;
    @NotNull
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    @NotNull
    @JoinColumn(name = "initiator_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User initiator;
    @NotNull
    @Embedded
    private Location location;
    @BooleanFlag
    @Column(name = "paid")
    private Boolean paid;
    @Column(name = "participant_limit")
    private Integer participantLimit;
    @Column(name = "published_on")
    private LocalDateTime publishedOn;
    @BooleanFlag
    @Column(name = "request_moderation")
    private Boolean requestModeration;
    @NotNull
    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private EventState state;
    @NotBlank
    @Size(min = 3, max = 120)
    @Column(name = "title")
    private String title;

    @PrePersist
    @PreUpdate
    public void setDefaultValues() {
        if (this.paid == null) {
            this.paid = false;
        }

        if (this.participantLimit == null) {
            this.participantLimit = 0;
        }

        if (this.requestModeration == null) {
            this.requestModeration = true;
        }
    }
}

