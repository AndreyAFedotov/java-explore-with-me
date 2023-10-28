package ru.practicum.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.enums.EventState;
import ru.practicum.enums.RequestStatus;
import ru.practicum.event.Event;
import ru.practicum.event.EventStorage;
import ru.practicum.exception.exceptions.AccessDeniedException;
import ru.practicum.exception.exceptions.NotFoundException;
import ru.practicum.request.RequestMapper;
import ru.practicum.request.RequestStorage;
import ru.practicum.request.dto.RequestDtoResponse;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestEvent;
import ru.practicum.user.User;
import ru.practicum.user.UserStorage;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestStorage requestStorage;
    private final UserStorage userStorage;
    private final EventStorage eventStorage;

    @Override
    public List<RequestDtoResponse> getRequestsByUser(Long userId) {
        checkUser(userId);
        List<Request> requests = requestStorage.findAllByRequesterId(userId);
        log.info("{} requests was found", requests.size());

        return requests.stream()
                .map(RequestMapper::toRequestDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RequestDtoResponse createRequestByUser(Long userId, Long eventId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        Event event = eventStorage.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event " + eventId + " not found"));

        if (requestStorage.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new AccessDeniedException("You cannot add a repeat request for event ID: " + eventId);
        }
        if (event.getInitiator().getId().equals(userId)) {
            throw new AccessDeniedException("Initiator of the event cannot add a request to participate in his event");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new AccessDeniedException("You cannot participate in an unpublished event");
        }
        RequestStatus requestStatus = RequestStatus.PENDING;
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            requestStatus = RequestStatus.CONFIRMED;
        }
        List<RequestEvent> confReq = requestStorage.getConfirmedRequests(List.of(eventId));
        if (event.getParticipantLimit() != 0 && confReq.size() >= event.getParticipantLimit()) {
            throw new AccessDeniedException("The event has reached the limit of participation requests");
        }

        Request request = Request.builder()
                .requester(user)
                .event(event)
                .status(requestStatus)
                .created(LocalDateTime.now())
                .build();
        Request resultRequest = requestStorage.save(request);

        log.info("New request was created by ID: {}", resultRequest.getId());
        return RequestMapper.toRequestDtoResponse(resultRequest);
    }

    @Override
    public RequestDtoResponse cancelRequestByUser(Long userId, Long requestId) {
        checkUser(userId);
        Request request = requestStorage.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));

        if (!request.getRequester().getId().equals(userId)) {
            throw new AccessDeniedException("The request ID " + requestId + " belongs to another user");
        }
        if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
            throw new AccessDeniedException("The request ID " + requestId + " is confirmed");
        }

        request.setStatus(RequestStatus.CANCELED);
        Request result = requestStorage.save(request);
        log.info("Request ID {} was canceled", result.getId());

        return RequestMapper.toRequestDtoResponse(request);
    }

    @Override
    public Map<Long, Integer> getConfirmedRequests(List<Event> events) {
        List<Long> ids = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        List<RequestEvent> req = requestStorage.getConfirmedRequests(ids);
        if (req.isEmpty()) {
            return new HashMap<>();
        }
        Map<Long, Integer> result = new HashMap<>();
        for (RequestEvent env : req) {
            result.put(env.getEventId(), env.getCount().intValue());
        }
        return result;
    }

    private void checkUser(Long userId) {
        if (!userStorage.existsUserById(userId)) {
            throw new NotFoundException("User " + userId + " not found");
        }
    }
}
