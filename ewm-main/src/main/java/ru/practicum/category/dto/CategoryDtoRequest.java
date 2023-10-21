package ru.practicum.category.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDtoRequest {

    @NotBlank
    @Length(min = 2, max = 100)
    private String name;
}
