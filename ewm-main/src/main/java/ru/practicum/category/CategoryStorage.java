package ru.practicum.category;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryStorage extends JpaRepository<Category, Long> {

}
