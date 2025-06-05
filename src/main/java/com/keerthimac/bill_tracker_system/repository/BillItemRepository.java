package com.keerthimac.bill_tracker_system.repository;

import com.keerthimac.bill_tracker_system.entity.BillItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillItemRepository extends JpaRepository<BillItem, Long> {

    /**
     * Finds all bill items associated with a specific purchase bill.
     * @param purchaseBillId The ID of the PurchaseBill.
     * @return A list of BillItems belonging to the specified purchase bill.
     */
    List<BillItem> findByPurchaseBillId(Long purchaseBillId);

    /**
     * Finds all bill items associated with a specific master material.
     * Useful for checking where a master material has been used.
     * @param masterMaterialId The ID of the MasterMaterial.
     * @return A list of BillItems using the specified master material.
     */
    List<BillItem> findByMasterMaterialId(Long masterMaterialId);

    /**
     * Checks if any BillItem is associated with a specific MasterMaterial.
     * More efficient than fetching the list if you only need to check existence.
     * @param masterMaterialId The ID of the MasterMaterial.
     * @return true if any BillItem uses this MasterMaterial, false otherwise.
     */
    boolean existsByMasterMaterialId(Long masterMaterialId);

    // The methods findByItemCategoryId(Long itemCategoryId) and
    // findByMaterialName(String materialName) have been removed
    // because BillItem no longer has direct itemCategoryId or materialName properties.

    // Queries based on material properties (like name or category) should now
    // primarily go through MasterMaterialRepository. If you need BillItems
    // based on those criteria, you would first query MasterMaterialRepository
    // and then use the resulting MasterMaterial IDs to query BillItemRepository
    // (e.g., using findByMasterMaterialIdIn(List<Long> masterMaterialIds) if needed).
}