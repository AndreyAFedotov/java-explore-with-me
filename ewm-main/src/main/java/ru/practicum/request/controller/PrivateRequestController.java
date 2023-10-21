package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.RequestDtoResponse;
import ru.practicum.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateRequestController {

    private final RequestService requestService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RequestDtoResponse> getRequestsByUser(@PathVariable Long userId) {
        log.info("private:requests - get requests for user ID {}", userId);
        return requestService.getRequestsByUser(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDtoResponse createRequestByUser(@PathVariable Long userId,
                                                  @RequestParam Long eventId) {
        log.info("private:requests - create request for event ID {} by user ID {}", eventId, userId);
        return requestService.createRequestByUser(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public RequestDtoResponse cancelRequestByUser(@PathVariable Long userId,
                                                  @PathVariable Long requestId) {
        log.info("private:requests - cancel request ID {} by user ID {}", requestId, userId);
        return requestService.cancelRequestByUser(userId, requestId);
    }

}
