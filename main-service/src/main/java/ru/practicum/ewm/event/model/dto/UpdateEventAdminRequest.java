package ru.practicum.ewm.event.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jdk.jfr.BooleanFlag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.model.Location;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.ewm.StatClient.DATE_TIME_FORMAT;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventAdminRequest {
    @Size(min = 20, max = 2000)
    private String annotation;
    private Integer category;
    @Size(min = 20, max = 7000)
    private String description;
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    private LocalDateTime eventDate;
    private Location location;
    @BooleanFlag
    private Boolean paid;
    private Integer participantLimit;
    @BooleanFlag
    private Boolean requestModeration;
    private AdminRequestState stateAction;
    @Size(min = 3, max = 120)
    private String title;
}
