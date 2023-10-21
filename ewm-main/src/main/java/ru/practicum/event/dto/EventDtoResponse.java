package ru.practicum.event.dto;

import lombok.*;
import ru.practicum.category.dto.CategoryDtoResponse;
import ru.practicum.enums.EventState;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.user.dto.UserDtoResponseShort;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDtoResponse {

    private String annotation;

    private CategoryDtoResponse category;

    private Integer confirmedRequests;

    private LocalDateTime createdOn;

    private String description;

    private LocalDateTime eventDate;

    private Long id;

    private UserDtoResponseShort initiator;

    private LocationDto location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private LocalDateTime publishedOn;

    private EventState state;

    private String title;

    private Integer views;
}
