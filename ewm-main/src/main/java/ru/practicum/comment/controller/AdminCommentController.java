package ru.practicum.comment.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDtoResponse;
import ru.practicum.comment.dto.CommentDtoResponseLong;
import ru.practicum.comment.dto.CommentDtoUpdateRequest;
import ru.practicum.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/admin/comments")
@AllArgsConstructor
@Slf4j
@Validated
public class AdminCommentController {

    private final CommentService commentService;

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByAdmin(@PathVariable Long commentId) {
        log.info("admin:comments - delete comment with id: {}", commentId);
        commentService.deleteCommentByAdmin(commentId);
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDtoResponse updateCommentByAdmin(@PathVariable Long commentId,
                                                   @Valid @RequestBody CommentDtoUpdateRequest request) {
        log.info("admin:comments - update comment with id: {}", commentId);
        return commentService.updateCommentByAdmin(commentId, request);
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDtoResponseLong> searchUserCommentsByAdmin(@PathVariable Long userId,
                                                                  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                                  @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("admin:comments - search comments of user id: {}", userId);
        return commentService.searchUserCommentsByAdmin(userId, from, size);
    }

}
