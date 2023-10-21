package ru.practicum.location.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationDto {

    private Float lat;

    private Float lon;

}
