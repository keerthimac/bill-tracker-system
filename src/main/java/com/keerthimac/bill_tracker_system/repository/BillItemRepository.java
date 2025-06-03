package com.keerthimac.bill_tracker_system.repository;

import com.keerthimac.bill_tracker_system.entity.BillItem;
import com.keerthimac.bill_tracker_system.entity.ItemCategory;
import com.keerthimac.bill_tracker_system.entity.PurchaseBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillItemRepository extends JpaRepository<BillItem, Long> {

    // Find all items belonging to a specific PurchaseBill
    List<BillItem> findByPurchaseBill(PurchaseBill purchaseBill);
    List<BillItem> findByPurchaseBillId(Long purchaseBillId);

    // Find all items belonging to a specific ItemCategory
    List<BillItem> findByItemCategory(ItemCategory itemCategory);
    List<BillItem> findByItemCategoryId(Long itemCategoryId);

    // Find items that have not yet received GRN confirmation
    List<BillItem> findByGrnReceivedForItemFalse();

    // Find items for a specific bill that have not yet received GRN confirmation
    List<BillItem> findByPurchaseBillIdAndGrnReceivedForItemFalse(Long purchaseBillId);

    // Find items by material name (e.g., for searching)
    List<BillItem> findByMaterialNameContainingIgnoreCase(String materialName);
}