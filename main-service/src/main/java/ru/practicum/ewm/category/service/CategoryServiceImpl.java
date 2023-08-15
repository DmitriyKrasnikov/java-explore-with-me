package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.model.CategoryMapper;
import ru.practicum.ewm.category.model.dto.CategoryDto;
import ru.practicum.ewm.category.model.dto.NewCategoryDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.pagination.FromSizePage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        log.info("Create category {}", newCategoryDto.getName());
        return categoryMapper.toDto(categoryRepository.save(categoryMapper.toCategory(newCategoryDto)));
    }

    @Transactional
    @Override
    public CategoryDto updateCategory(Integer catId, NewCategoryDto newCategoryDto) {
        log.info("Update category id = {}, name = {}", catId, newCategoryDto);

        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException(String.format("Category with id=%s was not found", catId)));

        categoryMapper.updateModel(newCategoryDto, category);
        return categoryMapper.toDto(category);
    }

    @Transactional
    @Override
    public void deleteCategory(Integer catId) {
        log.info("Delete category id = {}", catId);

        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException(String.format("Category with id=%s was not found", catId)));
        categoryRepository.delete(category);
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        log.info("Get categories from={}, size ={}", from, size);

        Pageable page = FromSizePage.ofFromSize(from, size);

        return categoryRepository.findAll(page).stream().map(categoryMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategory(Integer catId) {
        log.info("Get category id = {}", catId);

        return categoryMapper.toDto(categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException(String.format("Category with id=%s was not found", catId))));
    }
}
