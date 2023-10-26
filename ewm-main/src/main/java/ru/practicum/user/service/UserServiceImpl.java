package ru.practicum.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.exceptions.NotFoundException;
import ru.practicum.user.User;
import ru.practicum.user.UserMapper;
import ru.practicum.user.UserStorage;
import ru.practicum.user.dto.UserDtoRequest;
import ru.practicum.user.dto.UserDtoResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public List<UserDtoResponse> getUsersByAdmin(List<Long> id, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<User> users;
        if (id.isEmpty()) {
            users = userStorage.findAll(pageable).toList();
        } else {
            users = userStorage.getUsersByIdIn(id, pageable).toList();
        }
        log.info("Number of users {}", users.size());

        return users.stream()
                .map(UserMapper::toUserDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserDtoResponse createUserByAdmin(UserDtoRequest request) {
        User user = userStorage.save(UserMapper.toUser(request));
        log.info("New user with ID {} was ben created", user.getId());

        return UserMapper.toUserDtoResponse(user);
    }

    @Override
    public void deleteUserByAdmin(Long id) {
        if (isUserExists(id)) {
            userStorage.deleteById(id);
            log.info("User with ID {} was deleted", id);
        } else {
            throw new NotFoundException("User with id=" + id + " was not found");
        }
    }

    private Boolean isUserExists(Long userId) {
        return userStorage.existsById(userId);
    }
}
