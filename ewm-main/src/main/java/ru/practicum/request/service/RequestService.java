package ru.practicum.request.service;

import org.springframework.data.domain.Page;
import ru.practicum.event.Event;
import ru.practicum.request.dto.RequestDtoResponse;
import ru.practicum.request.model.RequestEvent;

import java.util.List;
import java.util.Map;

public interface RequestService {

    List<RequestDtoResponse> getRequestsByUser(Long userId);

    RequestDtoResponse createRequestByUser(Long userId, Long eventId);

    RequestDtoResponse cancelRequestByUser(Long userId, Long requestId);

    Map<Long, Integer> getConfirmedRequests(List<Event> events);
}
