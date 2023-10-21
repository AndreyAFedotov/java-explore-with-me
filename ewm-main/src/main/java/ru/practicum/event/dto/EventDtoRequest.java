package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import ru.practicum.enums.AdminStateAction;
import ru.practicum.location.dto.LocationDto;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDtoRequest {

    @NotBlank
    @Length(min = 5, max = 2000)
    private String annotation;

    @NotNull
    private Long category;

    @NotBlank
    @Length(min = 20, max = 5000)
    private String description;

    @NotNull
    @FutureOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull
    private LocationDto location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private AdminStateAction stateAction;

    @NotBlank
    @Length(min = 5, max = 255)
    private String title;

}
