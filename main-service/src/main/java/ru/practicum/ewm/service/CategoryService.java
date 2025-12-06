package ru.practicum.ewm.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto createCategory(NewCategoryDto dto);

    CategoryDto updateCategory(Long categoryId, NewCategoryDto dto);

    void deleteCategory(Long categoryId);

    public List<CategoryDto> getCategories(Pageable pageable);

    CategoryDto getCategory(Long categoryId);
}