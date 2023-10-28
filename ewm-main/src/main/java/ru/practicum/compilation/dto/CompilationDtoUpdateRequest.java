package ru.practicum.compilation.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompilationDtoUpdateRequest {

    private final List<Long> events = new ArrayList<>();

    private Boolean pinned;

    @Length(min = 1, max = 50)
    private String title;
}
