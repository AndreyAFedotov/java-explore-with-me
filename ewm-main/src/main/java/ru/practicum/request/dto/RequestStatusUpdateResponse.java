package ru.practicum.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class RequestStatusUpdateResponse {

    private final List<RequestDtoResponse> confirmedRequests = new ArrayList<>();

    private final List<RequestDtoResponse> rejectedRequests = new ArrayList<>();
}
