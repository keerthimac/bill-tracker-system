package com.keerthimac.bill_tracker_system.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillItemResponseDTO {
    private Long id;

    // Details from MasterMaterial
    private Long masterMaterialId;
    private String masterMaterialCode; // Optional, from MasterMaterial
    private String masterMaterialName;
    private String itemCategoryName; // Derived from MasterMaterial's ItemCategory

    private BigDecimal quantity;
    private String unit;
    private BigDecimal unitPrice;
    private BigDecimal itemTotalPrice;
    private boolean grnReceivedForItem;
    private String remarks;
}