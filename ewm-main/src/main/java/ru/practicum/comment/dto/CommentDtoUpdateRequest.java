package ru.practicum.comment.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDtoUpdateRequest {

    @NotBlank
    @Length(min = 5, max = 7000)
    private String text;
}
