package com.keerthimac.bill_tracker_system.repository;
import com.keerthimac.bill_tracker_system.entity.OverallGrnStatus;
import com.keerthimac.bill_tracker_system.entity.PurchaseBill;
import com.keerthimac.bill_tracker_system.entity.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PurchaseBillRepository extends JpaRepository<PurchaseBill, Long> {

    // Find bills by Site
    List<PurchaseBill> findBySite(Site site);

    // Find bills by Site Id (alternative to passing the Site object)
    List<PurchaseBill> findBySiteId(Long siteId);

    // Find bills by bill date range
    List<PurchaseBill> findByBillDateBetween(LocalDate startDate, LocalDate endDate);

    // Find bills for a specific site and date range
    List<PurchaseBill> findBySiteIdAndBillDateBetween(Long siteId, LocalDate startDate, LocalDate endDate);

    // Find bills by GRN status
    List<PurchaseBill> findByOverallGrnStatus(OverallGrnStatus status);

    // Find bills by supplier name (case-insensitive example)
    List<PurchaseBill> findBySupplierNameIgnoreCase(String supplierName);

    // Example of a more complex custom query using JPQL (Java Persistence Query Language)
    // This might be useful for your reporting needs later.
    // For instance, find bills where at least one item is still pending GRN (this is a conceptual example,
    // the actual implementation might involve joining with BillItem or checking a derived status)
    // @Query("SELECT pb FROM PurchaseBill pb WHERE pb.overallGrnStatus = :status AND pb.site = :site")
    // List<PurchaseBill> findBillsByStatusAndSite(@Param("status") OverallGrnStatus status, @Param("site") Site site);
}