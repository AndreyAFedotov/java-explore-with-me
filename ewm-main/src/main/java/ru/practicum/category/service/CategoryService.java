package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDtoRequest;
import ru.practicum.category.dto.CategoryDtoResponse;

import java.util.List;

public interface CategoryService {

    CategoryDtoResponse createCategoryByAdmin(CategoryDtoRequest request);

    void deleteCategoryByAdmin(Long id);

    CategoryDtoResponse updateCategoryByAdmin(Long id, CategoryDtoRequest request);

    List<CategoryDtoResponse> getCategoriesPublic(int from, int size);

    CategoryDtoResponse getCategoryPublic(Long catId);
}
