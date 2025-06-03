package com.keerthimac.bill_tracker_system.repository;
import com.keerthimac.bill_tracker_system.entity.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SiteRepository extends JpaRepository<Site, Long> {

    // Example of a custom query method (if you need to find a site by name)
    Optional<Site> findByName(String name);
}