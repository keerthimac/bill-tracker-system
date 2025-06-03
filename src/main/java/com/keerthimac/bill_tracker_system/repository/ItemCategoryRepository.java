package com.keerthimac.bill_tracker_system.repository;
import com.keerthimac.bill_tracker_system.entity.ItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Marks this as a Spring Data repository
public interface ItemCategoryRepository extends JpaRepository<ItemCategory, Long> {
    // JpaRepository<EntityType, IDType>

    // Spring Data JPA will automatically provide methods like:
    // save(ItemCategory entity)
    // findById(Long id)
    // findAll()
    // deleteById(Long id)
    // ...and many more.

    // You can also define custom query methods here if needed.
    // For example, to find a category by its name:
    Optional<ItemCategory> findByName(String name);
}
