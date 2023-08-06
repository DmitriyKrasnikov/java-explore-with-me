package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.model.dto.CategoryDto;
import ru.practicum.ewm.category.model.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    CategoryDto updateCategory(Integer catId, NewCategoryDto newCategoryDto);

    void deleteCategory(Integer catId);

    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategory(Integer catId);
}
