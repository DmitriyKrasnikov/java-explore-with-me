package ru.practicum.ewm.category.model;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import ru.practicum.ewm.category.model.dto.CategoryDto;
import ru.practicum.ewm.category.model.dto.NewCategoryDto;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toCategory(NewCategoryDto newCategoryDto);

    CategoryDto toDto(Category category);

    void updateModel(NewCategoryDto newCategoryDto, @MappingTarget Category category);
}
