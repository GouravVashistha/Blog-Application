package com.blog_app_apis.service;

import com.blog_app_apis.dtos.CategroyDTO;

import java.util.List;

public interface CategoryService {
    // create
    CategroyDTO createCategory(CategroyDTO categroyDTO);

    // update
    CategroyDTO updateCategory(CategroyDTO categroyDTO, Integer categoryId);

    //delete
    void deleteCategory(Integer categoryId);

    // get
    CategroyDTO getCategory(Integer categoryId);

    // getAll
    List<CategroyDTO> getCategories();
}
