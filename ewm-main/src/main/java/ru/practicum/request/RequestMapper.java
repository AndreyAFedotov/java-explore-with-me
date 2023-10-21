package ru.practicum.request;

import ru.practicum.request.dto.RequestDtoResponse;
import ru.practicum.request.model.Request;

import java.time.format.DateTimeFormatter;

public class RequestMapper {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static RequestDtoResponse toRequestDtoResponse(Request request) {
        return RequestDtoResponse.builder()
                .id(request.getId())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus().toString())
                .created(request.getCreated().format(FORMAT))
                .build();
    }
}
