package ru.practicum.user.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDtoRequest {

    @NotBlank
    @Length(min = 1, max = 300)
    private String name;

    @NotBlank
    @Email
    @Length(min = 6, max = 254)
    private String email;

}
