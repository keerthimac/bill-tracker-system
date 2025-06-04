package com.keerthimac.bill_tracker_system.controller;

import com.keerthimac.bill_tracker_system.dto.ItemCategoryDTO;
import com.keerthimac.bill_tracker_system.service.ItemCategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/item-categories") // Base path for all item category related APIs
public class ItemCategoryController {

    private final ItemCategoryService itemCategoryService;

    @Autowired
    public ItemCategoryController(ItemCategoryService itemCategoryService) {
        this.itemCategoryService = itemCategoryService;
    }

    // POST: Create a new Item Category
    @PostMapping
    public ResponseEntity<ItemCategoryDTO> createCategory(@Valid @RequestBody ItemCategoryDTO categoryDTO) {
        // Assuming ItemCategoryService handles any duplicate name checks if necessary
        ItemCategoryDTO createdCategory = itemCategoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    // GET: Retrieve an Item Category by its ID
    @GetMapping("/{id}")
    public ResponseEntity<ItemCategoryDTO> getCategoryById(@PathVariable Long id) {
        Optional<ItemCategoryDTO> categoryDTO = itemCategoryService.getCategoryById(id);
        return categoryDTO.map(ResponseEntity::ok) // If present, return 200 OK
                .orElseGet(() -> ResponseEntity.notFound().build()); // If not, return 404 Not Found
    }

    // GET: Retrieve all Item Categories
    @GetMapping
    public ResponseEntity<List<ItemCategoryDTO>> getAllCategories() {
        List<ItemCategoryDTO> categories = itemCategoryService.getAllCategories();
        if (categories.isEmpty()) {
            return ResponseEntity.noContent().build(); // Return 204 No Content if list is empty
        }
        return ResponseEntity.ok(categories);
    }

    // PUT: Update an existing Item Category
    @PutMapping("/{id}")
    public ResponseEntity<ItemCategoryDTO> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody ItemCategoryDTO categoryDTO) {
        // The service layer should handle ResourceNotFoundException if ID doesn't exist
        ItemCategoryDTO updatedCategory = itemCategoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.ok(updatedCategory);
    }

    // DELETE: Delete an Item Category by its ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        // The service layer should handle ResourceNotFoundException if ID doesn't exist
        // It might also handle logic if a category is in use and cannot be deleted
        itemCategoryService.deleteCategory(id);
        return ResponseEntity.noContent().build(); // Return 204 No Content on successful deletion
    }
}
