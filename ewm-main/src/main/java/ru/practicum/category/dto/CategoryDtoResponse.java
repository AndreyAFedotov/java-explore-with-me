package ru.practicum.category.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDtoResponse {

    private Long id;

    private String name;

}
