package com.juliandonati.api.mapper;

import com.juliandonati.api.domain.Category;
import com.juliandonati.api.dto.CategoryDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toDto(Category category);
    Category toEntity(CategoryDto categoryDto);
}
