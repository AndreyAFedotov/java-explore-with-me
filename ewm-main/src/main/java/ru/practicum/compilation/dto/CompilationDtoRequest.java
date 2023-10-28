package ru.practicum.compilation.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompilationDtoRequest {

    private final List<Long> events = new ArrayList<>();

    private Boolean pinned;

    @NotBlank
    @Length(min = 1, max = 50)
    private String title;
}
