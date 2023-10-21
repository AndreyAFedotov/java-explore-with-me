package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.enums.EventState;
import ru.practicum.event.dto.EventDtoRequest;
import ru.practicum.event.dto.EventDtoResponse;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.exceptions.ValidationException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminEventController {

    private final EventService eventService;

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventDtoResponse> getEventsByAdmin(@RequestParam(required = false) List<Long> users,
                                                   @RequestParam(required = false) List<EventState> states,
                                                   @RequestParam(required = false) List<Long> categories,
                                                   @RequestParam(required = false) String rangeStart,
                                                   @RequestParam(required = false) String rangeEnd,
                                                   @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                   @RequestParam(defaultValue = "10") @Positive int size)
        throws ValidationException {
        log.info("admin:events - get events list action");
        return eventService.getEventsByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDtoResponse updateEventByAdmin(@PathVariable Long eventId,
                                               @Valid @RequestBody EventDtoRequest request) {
        log.info("admin:events - update event with title: {}", request.getTitle());
        return eventService.updateEventByAdmin(eventId, request);
    }
}
