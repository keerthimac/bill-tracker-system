package com.keerthimac.bill_tracker_system.service.impl;

import com.keerthimac.bill_tracker_system.dto.ItemCategoryDTO;
import com.keerthimac.bill_tracker_system.entity.ItemCategory; // Ensure this import points to your model package
import com.keerthimac.bill_tracker_system.exception.DuplicateResourceException;
import com.keerthimac.bill_tracker_system.exception.ResourceNotFoundException;
import com.keerthimac.bill_tracker_system.mapper.ItemCategoryMapper; // Ensure this import points to your mapper package
import com.keerthimac.bill_tracker_system.repository.ItemCategoryRepository;
import com.keerthimac.bill_tracker_system.service.ItemCategoryService; // Ensure this import points to your service interface package
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ItemCategoryServiceImpl implements ItemCategoryService {

    private final ItemCategoryRepository itemCategoryRepository;
    private final ItemCategoryMapper itemCategoryMapper;

    @Autowired
    public ItemCategoryServiceImpl(ItemCategoryRepository itemCategoryRepository, ItemCategoryMapper itemCategoryMapper) {
        this.itemCategoryRepository = itemCategoryRepository;
        this.itemCategoryMapper = itemCategoryMapper;
    }

    @Override
    @Transactional
    public ItemCategoryDTO createCategory(ItemCategoryDTO categoryDTO) {
        // Check if a category with the same name already exists (case-insensitive)
        itemCategoryRepository.findByNameIgnoreCase(categoryDTO.getName()).ifPresent(existingCategory -> {
            throw new DuplicateResourceException("ItemCategory with name '" + categoryDTO.getName() + "' already exists.");
        });

        ItemCategory itemCategory = itemCategoryMapper.toEntity(categoryDTO);
        ItemCategory savedItemCategory = itemCategoryRepository.save(itemCategory);
        return itemCategoryMapper.toDto(savedItemCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ItemCategoryDTO> getCategoryById(Long id) {
        return itemCategoryRepository.findById(id)
                .map(itemCategoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ItemCategoryDTO> getCategoryByName(String name) {
        return itemCategoryRepository.findByNameIgnoreCase(name)
                .map(itemCategoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemCategoryDTO> getAllCategories() {
        List<ItemCategory> categories = itemCategoryRepository.findAll();
        return itemCategoryMapper.toDtoList(categories);
    }

    @Override
    @Transactional
    public ItemCategoryDTO updateCategory(Long id, ItemCategoryDTO categoryDTO) {
        ItemCategory existingCategory = itemCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ItemCategory not found with id: " + id));

        // Check if the new name conflicts with another existing category (excluding itself)
        if (!existingCategory.getName().equalsIgnoreCase(categoryDTO.getName())) {
            itemCategoryRepository.findByNameIgnoreCase(categoryDTO.getName()).ifPresent(conflictingCategory -> {
                if (!conflictingCategory.getId().equals(id)) { // If it's a different category
                    throw new DuplicateResourceException("Another ItemCategory with name '" + categoryDTO.getName() + "' already exists.");
                }
            });
        }

        existingCategory.setName(categoryDTO.getName());
        // If ItemCategoryDTO had more fields, you would map them here.
        // For more complex updates, MapStruct's @MappingTarget can be useful.

        ItemCategory updatedCategory = itemCategoryRepository.save(existingCategory);
        return itemCategoryMapper.toDto(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        if (!itemCategoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("ItemCategory not found with id: " + id + ". Cannot delete.");
        }
        // TODO: Before deleting, you should ideally check if this ItemCategory is being used by any BillItems.
        // If it is, you might want to prevent deletion or implement a soft delete mechanism.
        // Example check (requires BillItemRepository to have a method like countByItemCategoryId):
        // if (billItemRepository.countByItemCategoryId(id) > 0) {
        //     throw new IllegalStateException("Cannot delete ItemCategory with id: " + id + " as it is currently in use.");
        // }
        itemCategoryRepository.deleteById(id);
    }
}