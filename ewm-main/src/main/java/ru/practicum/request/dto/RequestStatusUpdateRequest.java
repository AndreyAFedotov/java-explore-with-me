package ru.practicum.request.dto;

import lombok.*;
import ru.practicum.enums.RequestStatus;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestStatusUpdateRequest {

    private final List<Long> requestIds = new ArrayList<>();

    private RequestStatus status;
}
