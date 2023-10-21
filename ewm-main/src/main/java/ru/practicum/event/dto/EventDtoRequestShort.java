package ru.practicum.event.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class EventDtoRequestShort {

    @NotBlank
    private final Long id;

}
