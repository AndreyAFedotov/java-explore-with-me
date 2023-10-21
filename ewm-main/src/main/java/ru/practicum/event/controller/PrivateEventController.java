package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventDtoRequest;
import ru.practicum.event.dto.EventDtoResponse;
import ru.practicum.event.dto.EventDtoUpdateRequest;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.RequestDtoResponse;
import ru.practicum.request.dto.RequestStatusUpdateRequest;
import ru.practicum.request.dto.RequestStatusUpdateResponse;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateEventController {

    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventDtoResponse> getUserEventsByUser(@PathVariable Long userId,
                                                      @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                      @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("private:events - get events for user ID: {}", userId);
        return eventService.getUserEventsByUser(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDtoResponse createEventByUser(@PathVariable Long userId,
                                              @Valid @RequestBody EventDtoRequest request) {
        log.info("private:events - create new event by user ID: {}", userId);
        return eventService.createEventByUser(userId, request);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDtoResponse getEventByUser(@PathVariable Long userId,
                                           @PathVariable Long eventId) {
        log.info("private:events - get event ID {} by user ID {}", eventId, userId);
        return eventService.getEventByUser(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDtoResponse updateEventByUser(@PathVariable Long userId,
                                              @PathVariable Long eventId,
                                              @Valid @RequestBody EventDtoUpdateRequest request) {
        log.info("private:events - update event ID {} by user ID {}", eventId, userId);
        return eventService.updateEventByUser(userId, eventId, request);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<RequestDtoResponse> getEventRequestsByUser(@PathVariable Long userId,
                                                           @PathVariable Long eventId) {
        log.info("private:events - get requests for event ID {} by user ID {}", eventId, userId);
        return eventService.getEventRequestsByUser(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<RequestStatusUpdateResponse> updateEventRequestsByUser(@PathVariable Long userId,
                                                                       @PathVariable Long eventId,
                                                                       @Valid @RequestBody RequestStatusUpdateRequest request) {
        log.info("private:events - update requests status for event ID {} by user ID {}", eventId, userId);
        return eventService.updateEventRequestsByUser(userId, eventId, request);
    }

}
