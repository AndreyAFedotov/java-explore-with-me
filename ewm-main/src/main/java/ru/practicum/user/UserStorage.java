package ru.practicum.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserStorage extends JpaRepository<User, Long> {

    Page<User> getUsersByIdIn(List<Long> id, Pageable pageable);

    Boolean existsUserById(Long userId);

}
