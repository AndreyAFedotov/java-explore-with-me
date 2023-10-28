package ru.practicum.request.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestEvent {

    private Long eventId;

    private Long count;
}
