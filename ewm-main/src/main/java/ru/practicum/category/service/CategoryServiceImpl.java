package ru.practicum.category.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.Category;
import ru.practicum.category.CategoryMapper;
import ru.practicum.category.CategoryStorage;
import ru.practicum.category.dto.CategoryDtoRequest;
import ru.practicum.category.dto.CategoryDtoResponse;
import ru.practicum.event.EventStorage;
import ru.practicum.exception.exceptions.AccessDeniedException;
import ru.practicum.exception.exceptions.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryStorage categoryStorage;
    private final EventStorage eventStorage;

    @Override
    public CategoryDtoResponse createCategoryByAdmin(CategoryDtoRequest request) {
        Category category = categoryStorage.save(CategoryMapper.toCategory(request));
        log.info("New category with ID {} was ben created", category.getId());

        return CategoryMapper.toCategoryDtoResponse(category);
    }

    @Override
    public void deleteCategoryByAdmin(Long id) {
        if (isCategoryExists(id)) {
            if (eventStorage.existsByCategoryId(id)) {
                throw new AccessDeniedException("The category is not empty");
            } else {
                categoryStorage.deleteById(id);
                log.info(("Category with id={} was deleted"), id);
            }
        } else {
            throw new NotFoundException("Category with id=" + id + " was not found");
        }
    }

    @Override
    public CategoryDtoResponse updateCategoryByAdmin(Long id, CategoryDtoRequest request) {
        Category category = categoryStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with id=" + id + " was not found"));

        if (!category.getName().equals(request.getName()) && categoryStorage.existsByNameIgnoreCase(request.getName())) {
            throw new AccessDeniedException("Category name duplication for: " + request.getName());
        }

        category.setName(request.getName());
        Category updated = categoryStorage.save(category);
        log.info("Category with id={} was not updated", updated.getId());

        return CategoryMapper.toCategoryDtoResponse(updated);
    }

    @Override
    public List<CategoryDtoResponse> getCategoriesPublic(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        Page<Category> categories = categoryStorage.findAll(pageable);

        log.info("{} categories found", categories.getSize());

        return categories.stream()
                .map(CategoryMapper::toCategoryDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDtoResponse getCategoryPublic(Long catId) {
        Category category = categoryStorage.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));

        log.info("Category {} was found", category.getId());

        return CategoryMapper.toCategoryDtoResponse(category);
    }

    private Boolean isCategoryExists(Long id) {
        return categoryStorage.existsById(id);
    }
}
