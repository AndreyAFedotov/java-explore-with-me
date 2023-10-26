package ru.practicum.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.UserDtoRequest;
import ru.practicum.user.dto.UserDtoResponse;
import ru.practicum.user.service.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDtoResponse> getUsersByAdmin(@RequestParam(required = false) List<Long> ids,
                                                 @RequestParam(defaultValue = "0") int from,
                                                 @RequestParam(defaultValue = "10") int size) {
        if (ids == null) {
            ids = new ArrayList<>();
        }
        log.info("admin:users - request user info. User count: {}", ids.size());
        return userService.getUsersByAdmin(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDtoResponse createUserByAdmin(@Valid @RequestBody UserDtoRequest request) {
        log.info("admin:users - create new user with email: {}", request.getEmail());
        return userService.createUserByAdmin(request);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserByAdmin(@PathVariable Long userId) {
        log.info("admin:users - delete user with id: {}", userId);
        userService.deleteUserByAdmin(userId);
    }
}
