package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatsDtoRequest;
import ru.practicum.StatsDtoResponse;
import ru.practicum.exception.ValidationException;
import ru.practicum.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class StatsController {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void createHit(@Valid @RequestBody StatsDtoRequest request) {
        log.info("Create Hit");
        statsService.createHit(request);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<StatsDtoResponse>> getStats(@RequestParam String start,
                                                           @RequestParam String end,
                                                           @RequestParam(required = false) List<String> uris,
                                                           @RequestParam(defaultValue = "false") boolean unique
    ) throws ValidationException {
        log.info("Stats request");

        LocalDateTime startDateTime = LocalDateTime.parse(start, FORMAT);
        LocalDateTime endDateTime = LocalDateTime.parse(end, FORMAT);
        if (startDateTime.isAfter(endDateTime)) {
            throw new ValidationException("Start must be before end");
        }

        return new ResponseEntity<>(statsService.getStats(startDateTime, endDateTime, uris, unique), HttpStatus.OK);
    }

}
