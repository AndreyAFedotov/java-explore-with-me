package ru.practicum.category.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDtoResponse;
import ru.practicum.category.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/categories")
@AllArgsConstructor
@Slf4j
@Validated
public class PublicCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDtoResponse> getCategoriesPublic(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                         @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("public:categories - get categories");
        return categoryService.getCategoriesPublic(from, size);
    }

    @GetMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDtoResponse getCategoryPublic(@PathVariable Long catId) {
        log.info("public:categories - get category by ID: {}", catId);
        return categoryService.getCategoryPublic(catId);
    }


}
