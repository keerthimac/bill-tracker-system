package com.keerthimac.bill_tracker_system.repository;
import com.keerthimac.bill_tracker_system.entity.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // If you add search methods
import java.util.Optional;

@Repository
public interface SiteRepository extends JpaRepository<Site, Long> {

    // You DECLARE this method signature:
    Optional<Site> findByNameIgnoreCase(String name);

    // You could also declare other derived queries:
    // List<Site> findByLocationContainingIgnoreCase(String locationFragment);
}