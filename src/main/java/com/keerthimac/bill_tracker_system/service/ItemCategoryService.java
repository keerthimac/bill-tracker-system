package com.keerthimac.bill_tracker_system.service;

import com.keerthimac.bill_tracker_system.dto.ItemCategoryDTO;

import java.util.List;
import java.util.Optional;

public interface ItemCategoryService {
    ItemCategoryDTO createCategory(ItemCategoryDTO categoryDTO);
    Optional<ItemCategoryDTO> getCategoryById(Long id);
    Optional<ItemCategoryDTO> getCategoryByName(String name);
    List<ItemCategoryDTO> getAllCategories();
    ItemCategoryDTO updateCategory(Long id, ItemCategoryDTO categoryDTO);
    void deleteCategory(Long id);
}
