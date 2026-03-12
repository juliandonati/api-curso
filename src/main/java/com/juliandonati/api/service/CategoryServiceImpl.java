package com.juliandonati.api.service;

import com.juliandonati.api.domain.Category;
import com.juliandonati.api.exception.ResourceNotFoundException;
import com.juliandonati.api.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public Category findByName(String name) throws ResourceNotFoundException {
        return categoryRepository.findByName(name).orElseThrow(() -> new ResourceNotFoundException("No se ha logrado encontrar la categoría: " + name));
    }

    @Override
    @Transactional(readOnly = true)
    public Category findById(Long id) throws ResourceNotFoundException {
        return categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No se ha logrado encontrar la categoría de id: " + id));
    }

    @Override
    @Transactional
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public Category update(Category category, Long id) throws ResourceNotFoundException {
        Category categoryToUpdate = categoryRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("La categoría de id: " + id + " no existe.")
        );

        categoryToUpdate.setName(category.getName());
        categoryToUpdate.setDescription(category.getDescription());

        return categoryRepository.save(categoryToUpdate);

    }

    @Override
    @Transactional
    public void deleteById(Long id) throws ResourceNotFoundException {
        if(!categoryRepository.existsById(id))
            throw new ResourceNotFoundException("La categoría de id: " + id + " no existe.");
        categoryRepository.deleteById(id);
    }
}
