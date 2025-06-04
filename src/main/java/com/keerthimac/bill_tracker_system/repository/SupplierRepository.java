package com.keerthimac.bill_tracker_system.repository;

import com.keerthimac.bill_tracker_system.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository // Indicates that this is a Spring Data repository bean
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    // JpaRepository<EntityType, IDType>

    // Spring Data JPA automatically provides methods like:
    // - save(Supplier entity)
    // - findById(Long id)
    // - findAll()
    // - deleteById(Long id)
    // - count()
    // - existsById(Long id)
    // ...and many more.

    // You can add custom query methods here if needed.
    // For example, to find a supplier by its exact name (case-insensitive):
    Optional<Supplier> findByNameIgnoreCase(String name);

    // Or to find suppliers whose names contain a certain string (case-insensitive), useful for searching:
    List<Supplier> findByNameContainingIgnoreCase(String nameFragment);

    // Find by email (if you need to ensure email is unique or for lookups)
    Optional<Supplier> findByEmailIgnoreCase(String email);
}
