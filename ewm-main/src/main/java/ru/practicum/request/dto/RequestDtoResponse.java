package ru.practicum.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestDtoResponse {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private String created;

    private Long event;

    private Long id;

    private Long requester;

    private String status;
}
