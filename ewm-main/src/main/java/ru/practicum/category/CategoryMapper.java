package ru.practicum.category;

import lombok.experimental.UtilityClass;
import ru.practicum.category.dto.CategoryDtoRequest;
import ru.practicum.category.dto.CategoryDtoResponse;

@UtilityClass
public class CategoryMapper {
    public static Category toCategory(CategoryDtoRequest request) {
        return Category.builder()
                .name(request.getName())
                .build();
    }

    public static CategoryDtoResponse toCategoryDtoResponse(Category category) {
        return CategoryDtoResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
