package com.blog_app_apis.serviceImpl;

import com.blog_app_apis.Entity.Category;
import com.blog_app_apis.dtos.CategroyDTO;
import com.blog_app_apis.exceptions.ResourceNotFoundException;
import com.blog_app_apis.repository.CategoryRepository;
import com.blog_app_apis.service.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImple implements CategoryService {

    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    ModelMapper modelMapper;

    @Override
    public CategroyDTO createCategory(CategroyDTO categroyDTO) {
        Category category = modelMapper.map(categroyDTO, Category.class);
        try {
            Category saveCategory = categoryRepository.save(category);
            return modelMapper.map(category, CategroyDTO.class);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CategroyDTO updateCategory(CategroyDTO categroyDTO, Integer categoryId) {
        Category category = getCategoryOrThrow(categoryId);
        if (categroyDTO.getCategoryTitle() != null) {
            category.setCategoryTitle(categroyDTO.getCategoryTitle());
        }
        if (categroyDTO.getCategoryDecription() != null) {
            category.setCategoryDecription(categroyDTO.getCategoryDecription());
        }
        try {
            Category savaCategory = categoryRepository.save(category);
            return modelMapper.map(savaCategory, CategroyDTO.class);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteCategory(Integer categoryId) {
        Category category = getCategoryOrThrow(categoryId);
        this.categoryRepository.delete(category);
    }

    @Override
    public CategroyDTO getCategory(Integer categoryId) {
        Category category = getCategoryOrThrow(categoryId);
        return modelMapper.map(category, CategroyDTO.class);
    }

    @Override
    public List<CategroyDTO> getCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(category -> modelMapper.map(category, CategroyDTO.class))
                .toList();
    }

    private Category getCategoryOrThrow(Integer categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
    }
}
