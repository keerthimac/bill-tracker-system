package com.keerthimac.bill_tracker_system.repository;

import com.keerthimac.bill_tracker_system.entity.PriceRevisionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceRevisionLogRepository extends JpaRepository<PriceRevisionLog, Long> {
    // Find all revisions for a specific supplier material price entry
    List<PriceRevisionLog> findBySupplierMaterialPriceIdOrderByChangeTimestampDesc(Long supplierMaterialPriceId);
}