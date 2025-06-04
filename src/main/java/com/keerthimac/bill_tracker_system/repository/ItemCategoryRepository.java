package com.keerthimac.bill_tracker_system.repository;
import com.keerthimac.bill_tracker_system.entity.ItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // For potential future search methods
import java.util.Optional;

@Repository
public interface ItemCategoryRepository extends JpaRepository<ItemCategory, Long> {

    /**
     * Finds an item category by its name, ignoring case.
     * @param name The name of the item category.
     * @return An Optional containing the found ItemCategory or empty if not found.
     */
    Optional<ItemCategory> findByNameIgnoreCase(String name);

    /**
     * Finds item categories whose names contain the given fragment, ignoring case.
     * Useful for search functionality.
     * @param nameFragment The fragment to search for within item category names.
     * @return A list of matching ItemCategory entities.
     */
    // List<ItemCategory> findByNameContainingIgnoreCase(String nameFragment); // You can uncomment this if you plan to add a search service method
}
