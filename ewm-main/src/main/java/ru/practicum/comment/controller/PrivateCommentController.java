package ru.practicum.comment.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDtoRequest;
import ru.practicum.comment.dto.CommentDtoResponse;
import ru.practicum.comment.dto.CommentDtoUpdateRequest;
import ru.practicum.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/user/{userId}/comments")
@AllArgsConstructor
@Slf4j
@Validated
public class PrivateCommentController {

    private final CommentService commentService;

    @PostMapping("/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDtoResponse createCommentByUser(@PathVariable Long userId,
                                                  @PathVariable Long eventId,
                                                  @Valid @RequestBody CommentDtoRequest request) {
        log.info("private:comments - create comment for event ID {} by user ID {}", eventId, userId);
        return commentService.createCommentByUser(userId, eventId, request);
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDtoResponse updateCommentByUser(@PathVariable Long userId,
                                                  @PathVariable Long commentId,
                                                  @Valid @RequestBody CommentDtoUpdateRequest request) {
        log.info("private:comments - update comment ID {} by user ID {}", commentId, userId);
        return commentService.updateCommentByUser(userId, commentId, request);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByUser(@PathVariable Long userId,
                                    @PathVariable Long commentId) {
        log.info("private:comments - delete comment ID {} by user ID {}", commentId, userId);
        commentService.deleteCommentByUser(userId, commentId);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDtoResponse> getEventCommentsByUser(@PathVariable Long userId,
                                                           @PathVariable Long eventId,
                                                           @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                           @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("private:comments - get comments for event ID {} by user ID {}", eventId, userId);
        return commentService.getEventCommentsByUser(userId, eventId, from, size);
    }

}
