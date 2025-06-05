package com.keerthimac.bill_tracker_system.repository;

import com.keerthimac.bill_tracker_system.entity.SupplierMaterialPrice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierMaterialPriceRepository extends JpaRepository<SupplierMaterialPrice, Long> {

    /**
     * Finds all price entries for a specific supplier.
     * @param supplierId The ID of the supplier.
     * @return A list of SupplierMaterialPrice entries for the given supplier.
     */
    List<SupplierMaterialPrice> findBySupplierId(Long supplierId);

    /**
     * Finds all price entries for a specific master material across all suppliers.
     * @param masterMaterialId The ID of the master material.
     * @return A list of SupplierMaterialPrice entries for the given master material.
     */
    List<SupplierMaterialPrice> findByMasterMaterialId(Long masterMaterialId);

    /**
     * Finds all price entries for a specific combination of supplier and master material.
     * This can be used to view price history or future prices for a supplier-item pair.
     * @param supplierId The ID of the supplier.
     * @param masterMaterialId The ID of the master material.
     * @return A list of SupplierMaterialPrice entries.
     */
    List<SupplierMaterialPrice> findBySupplierIdAndMasterMaterialIdOrderByEffectiveFromDateDesc(Long supplierId, Long masterMaterialId);

    /**
     * Finds the currently active price for a specific supplier, master material, and unit on a given target date.
     * It prioritizes the one with the most recent effectiveFromDate if multiple could be considered active
     * (though ideally, your data/logic should prevent overlapping active periods for the exact same criteria).
     * @param supplierId The ID of the supplier.
     * @param masterMaterialId The ID of the master material.
     * @param unit The unit of measure.
     * @param targetDate The date for which to find the active price.
     * @return An Optional containing the active SupplierMaterialPrice, or empty if none is found.
     */
    @Query("SELECT smp FROM SupplierMaterialPrice smp " +
            "WHERE smp.supplier.id = :supplierId " +
            "AND smp.masterMaterial.id = :masterMaterialId " +
            "AND smp.unit = :unit " +
            "AND smp.isActive = true " +
            "AND smp.effectiveFromDate <= :targetDate " +
            "AND (smp.effectiveToDate IS NULL OR smp.effectiveToDate >= :targetDate) " +
            "ORDER BY smp.effectiveFromDate DESC, smp.createdAt DESC") // Prioritize most recent effective start date, then creation
    List<SupplierMaterialPrice> findActivePriceAsList( // Use List and get first element in service
                                                       @Param("supplierId") Long supplierId,
                                                       @Param("masterMaterialId") Long masterMaterialId,
                                                       @Param("unit") String unit,
                                                       @Param("targetDate") LocalDate targetDate,
                                                       Pageable pageable // Use Pageable to limit results, e.g., PageRequest.of(0, 1)
    );

    // Helper to use in service to get Optional<SupplierMaterialPrice>
    default Optional<SupplierMaterialPrice> findActivePrice(
            Long supplierId, Long masterMaterialId, String unit, LocalDate targetDate) {
        List<SupplierMaterialPrice> results = findActivePriceAsList(
                supplierId, masterMaterialId, unit, targetDate, Pageable.ofSize(1)
        );
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }


    /**
     * Finds price entries that overlap with a given date range for a specific supplier, material, and unit,
     * excluding a specific price entry ID (useful for validation during updates).
     * An overlap occurs if:
     * (smp.effectiveFromDate <= newEffectiveToDate OR newEffectiveToDate IS NULL) AND
     * (smp.effectiveToDate >= newEffectiveFromDate OR smp.effectiveToDate IS NULL)
     * @param supplierId The supplier ID.
     * @param masterMaterialId The master material ID.
     * @param unit The unit.
     * @param newEffectiveFromDate The start date of the new/updated price entry.
     * @param newEffectiveToDate The end date of the new/updated price entry (can be null).
     * @param excludeId The ID of the current price entry being updated (to exclude it from the overlap check),
     * can be null if checking for a new entry.
     * @return A list of overlapping price entries.
     */
    @Query("SELECT smp FROM SupplierMaterialPrice smp " +
            "WHERE smp.supplier.id = :supplierId " +
            "AND smp.masterMaterial.id = :masterMaterialId " +
            "AND smp.unit = :unit " +
            "AND smp.isActive = true " + // Consider only active overlaps
            "AND (smp.id <> :excludeId OR :excludeId IS NULL) " + // Exclude self if excludeId is provided
            "AND smp.effectiveFromDate < COALESCE(:newEffectiveToDate, smp.effectiveFromDate + 1 DAY) " + // existing starts before new ends (or new end is open)
            "AND COALESCE(smp.effectiveToDate, :newEffectiveFromDate + 1 DAY) > :newEffectiveFromDate")   // existing ends after new starts (or existing end is open)
    List<SupplierMaterialPrice> findOverlappingPrices(
            @Param("supplierId") Long supplierId,
            @Param("masterMaterialId") Long masterMaterialId,
            @Param("unit") String unit,
            @Param("newEffectiveFromDate") LocalDate newEffectiveFromDate,
            @Param("newEffectiveToDate") LocalDate newEffectiveToDate, // Can be null for an open-ended new price
            @Param("excludeId") Long excludeId // ID of the price entry being updated/checked, null for new entries
    );

}