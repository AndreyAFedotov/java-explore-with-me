package ru.practicum.user;

import lombok.experimental.UtilityClass;
import ru.practicum.user.dto.UserDtoRequest;
import ru.practicum.user.dto.UserDtoResponse;
import ru.practicum.user.dto.UserDtoResponseShort;

@UtilityClass
public class UserMapper {

    public static User toUser(UserDtoRequest request) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .build();
    }

    public static UserDtoResponse toUserDtoResponse(User user) {
        return UserDtoResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static UserDtoResponseShort toUserDtoResponseShort(User user) {
        return UserDtoResponseShort.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}
