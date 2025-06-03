package com.keerthimac.bill_tracker_system.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class BillItemRequestDTO {
    private String materialName;
    private Long itemCategoryId; // ID of the ItemCategory
    private BigDecimal quantity;
    private String unit;
    private BigDecimal unitPrice;
    // No grnReceivedForItem here, as it's usually updated later
    // No itemTotalPrice here, it will be calculated
}
