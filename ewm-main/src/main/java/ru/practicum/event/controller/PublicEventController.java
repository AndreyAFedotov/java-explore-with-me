package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventDtoResponse;
import ru.practicum.event.dto.EventDtoResponseShort;
import ru.practicum.event.service.EventService;
import ru.practicum.stat.aop.SaveStatistic;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicEventController {

    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @SaveStatistic
    public List<EventDtoResponseShort> getEventsPublic(@RequestParam(required = false) String text,
                                                       @RequestParam(required = false) List<Long> categories,
                                                       @RequestParam(required = false) Boolean paid,
                                                       @RequestParam(required = false) String rangeStart,
                                                       @RequestParam(required = false) String rangeEnd,
                                                       @RequestParam(defaultValue = "false") Boolean available,
                                                       @RequestParam(required = false) String sort,
                                                       @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                       @RequestParam(defaultValue = "10") @Positive int size,
                                                       HttpServletRequest request) {
        log.info("public:events - get filtered events.Path: {}", request.getRequestURI());
        return eventService.getEventsPublic(text, categories, paid, rangeStart, rangeEnd, available, sort, from, size,
                request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @SaveStatistic
    public EventDtoResponse getEventPublic(@PathVariable Long id, HttpServletRequest request) {
        log.info("public:events - get event with ID: {}, Path: {}", id, request.getRequestURI());
        return eventService.getEventPublic(id, request);
    }

}
