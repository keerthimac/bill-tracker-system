package com.keerthimac.bill_tracker_system.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class BillItemResponseDTO {
    private Long id;
    private String materialName;
    private ItemCategoryDTO itemCategory;
    private BigDecimal quantity;
    private String unit;
    private BigDecimal unitPrice;
    private BigDecimal itemTotalPrice;
    private boolean grnReceivedForItem;
    private String remarks;
}
