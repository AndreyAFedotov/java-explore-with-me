package ru.practicum.compilation.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompilationDtoResponse {

    private Long id;

    private List<Long> events;

    private Boolean pinned;

    private String title;
}
