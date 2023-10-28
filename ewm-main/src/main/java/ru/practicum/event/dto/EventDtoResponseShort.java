package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.category.dto.CategoryDtoResponse;
import ru.practicum.user.dto.UserDtoResponseShort;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDtoResponseShort {

    private String annotation;

    private CategoryDtoResponse category;

    private Integer confirmedRequests;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Long id;

    private UserDtoResponseShort initiator;

    private Boolean paid;

    private String title;

    private Integer views;

}
