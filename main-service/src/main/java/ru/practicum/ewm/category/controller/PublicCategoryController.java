package ru.practicum.ewm.category.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.model.dto.CategoryDto;
import ru.practicum.ewm.category.service.CategoryService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/categories")
@AllArgsConstructor
public class PublicCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getCategories(@RequestParam(name = "from", defaultValue = "0") Integer from,
                                           @RequestParam(name = "size", defaultValue = "10") Integer size) {

        List<CategoryDto> categoryDtoList = categoryService.getCategories(from, size);

        if (categoryDtoList == null) {
            return new ArrayList<>();
        }

        return categoryDtoList;
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategory(@PathVariable(name = "catId") Integer catId) {
        return categoryService.getCategory(catId);
    }

}
