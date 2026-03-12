package com.juliandonati.api.controller;

import com.juliandonati.api.domain.Category;
import com.juliandonati.api.dto.CategoryDto;
import com.juliandonati.api.mapper.CategoryMapper;
import com.juliandonati.api.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<CategoryDto>> getAllCategories(){
        List<CategoryDto> categoryDtos = categoryService.findAll().stream()
                .map(categoryMapper::toDto)
                .toList();

        return new ResponseEntity<>(categoryDtos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long id){
        CategoryDto categoryDto = categoryMapper.toDto(categoryService.findById(id));

        return ResponseEntity.ok(categoryDto);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<CategoryDto> createCategory(@RequestBody @Valid CategoryDto categoryDto){
        Category categoryToCreate = categoryMapper.toEntity(categoryDto);

        Category createdCategory = categoryService.save(categoryToCreate);

        return new ResponseEntity<>(categoryMapper.toDto(createdCategory), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long id, @RequestBody @Valid CategoryDto categoryDto){
        Category categoryToUpdate = categoryMapper.toEntity(categoryDto);

        Category updatedCategory = categoryService.update(categoryToUpdate, id);

        return ResponseEntity.ok(categoryMapper.toDto(updatedCategory));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<String> deleteCategoryById(@PathVariable Long id){
        categoryService.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
