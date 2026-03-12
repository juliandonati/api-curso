package com.juliandonati.api.service;

import com.juliandonati.api.domain.Category;
import com.juliandonati.api.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    List<Category> findAll();
    boolean existsByName(String name);
    Category findByName(String name) throws ResourceNotFoundException;
    Category findById(Long id) throws ResourceNotFoundException;
    Category save(Category category);
    Category update(Category category, Long id) throws ResourceNotFoundException;
    void deleteById(Long id) throws ResourceNotFoundException;
}
