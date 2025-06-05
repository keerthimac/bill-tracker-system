package com.keerthimac.bill_tracker_system.repository; // Your repository package

import com.keerthimac.bill_tracker_system.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    /**
     * Finds a brand by its name, ignoring case.
     * Since brand names are unique, this should return at most one result.
     * @param name The name of the brand to search for.
     * @return An Optional containing the found Brand or empty if not found.
     */
    Optional<Brand> findByNameIgnoreCase(String name);

    /**
     * Finds brands whose names contain the given fragment, ignoring case.
     * Useful for search functionality (e.g., type-ahead suggestions).
     * @param nameFragment The fragment to search for within brand names.
     * @return A list of matching Brand entities.
     */
    List<Brand> findByNameContainingIgnoreCase(String nameFragment);

    /**
     * Checks if a brand with the given name exists, ignoring case.
     * More efficient than fetching the whole entity if you only need to check for existence.
     * @param name The name to check.
     * @return true if a brand with the name exists, false otherwise.
     */
    boolean existsByNameIgnoreCase(String name);

}
