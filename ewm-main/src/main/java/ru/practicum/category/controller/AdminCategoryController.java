package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDtoRequest;
import ru.practicum.category.dto.CategoryDtoResponse;
import ru.practicum.category.service.CategoryService;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDtoResponse createCategoryByAdmin(@Valid @RequestBody CategoryDtoRequest request) {
        log.info("admin:categories - create new categories with name: {}", request.getName());
        return categoryService.createCategoryByAdmin(request);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoryByAdmin(@PathVariable Long catId) {
        log.info("admin:categories - delete category with ID: {}", catId);
        categoryService.deleteCategoryByAdmin(catId);
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDtoResponse updateCategoryByAdmin(@PathVariable Long catId,
                                                     @Valid @RequestBody CategoryDtoRequest request) {
        log.info("admin:categories - update category with ID: {}", catId);
        return categoryService.updateCategoryByAdmin(catId, request);
    }
}
