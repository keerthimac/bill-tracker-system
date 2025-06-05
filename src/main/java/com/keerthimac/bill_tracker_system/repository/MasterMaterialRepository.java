package com.keerthimac.bill_tracker_system.repository;

import com.keerthimac.bill_tracker_system.entity.MasterMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MasterMaterialRepository extends JpaRepository<MasterMaterial, Long> {

    /**
     * Finds a master material by its unique material code, ignoring case.
     * Useful for checking uniqueness or retrieving by code.
     * @param materialCode The material code to search for.
     * @return An Optional containing the found MasterMaterial or empty if not found.
     */
    Optional<MasterMaterial> findByMaterialCodeIgnoreCase(String materialCode);

    /**
     * Finds a master material by its name, ignoring case.
     * Useful for checking for duplicate names or retrieving by exact name.
     * @param name The name of the master material.
     * @return An Optional containing the found MasterMaterial or empty if not found.
     */
    Optional<MasterMaterial> findByNameIgnoreCase(String name);

    /**
     * Finds master materials whose names contain the given fragment, ignoring case.
     * Useful for implementing search functionality.
     * @param nameFragment The fragment to search for within material names.
     * @return A list of matching MasterMaterial entities.
     */
    List<MasterMaterial> findByNameContainingIgnoreCase(String nameFragment);

    /**
     * Finds all master materials belonging to a specific item category.
     * @param itemCategoryId The ID of the item category.
     * @return A list of MasterMaterial entities belonging to the specified category.
     */
    List<MasterMaterial> findByItemCategoryId(Long itemCategoryId);

    /**
     * Checks if a master material with the given material code exists, ignoring case.
     * More efficient than fetching the whole entity if you only need to check existence.
     * @param materialCode The material code to check.
     * @return true if a material with the code exists, false otherwise.
     */
    boolean existsByMaterialCodeIgnoreCase(String materialCode);

    /**
     * Checks if a master material with the given name exists, ignoring case.
     * @param name The name to check.
     * @return true if a material with the name exists, false otherwise.
     */
    boolean existsByNameIgnoreCase(String name);

    // Add this method declaration:
    //boolean existsByBrandId(Long brandId); // <<< ADD THIS LINE
    boolean existsByBrand_Id(Long brandId);

}
