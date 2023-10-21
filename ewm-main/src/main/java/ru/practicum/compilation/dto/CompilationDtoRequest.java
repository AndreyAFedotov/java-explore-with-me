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

    private Boolean pined;

    @NotBlank
    @Length(min = 1, max = 100)
    private String title;
}
