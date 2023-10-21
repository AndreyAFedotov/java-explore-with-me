package ru.practicum.user.service;

import ru.practicum.user.dto.UserDtoRequest;
import ru.practicum.user.dto.UserDtoResponse;

import java.util.List;

public interface UserService {

    List<UserDtoResponse> getUsersByAdmin(List<Long> id, int from, int size);

    UserDtoResponse createUserByAdmin(UserDtoRequest request);

    void deleteUserByAdmin(Long id);

}
