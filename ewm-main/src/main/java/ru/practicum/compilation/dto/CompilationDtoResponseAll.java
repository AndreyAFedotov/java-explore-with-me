package ru.practicum.compilation.dto;

import lombok.*;
import ru.practicum.event.dto.EventDtoResponseShort;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompilationDtoResponseAll {

    private Collection<EventDtoResponseShort> events;

    private Long id;

    private Boolean pinned;

    private String title;

}
