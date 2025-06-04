package com.keerthimac.bill_tracker_system.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PurchaseBillResponseDTO {
    private Long id;
    private String billNumber;
    private LocalDate billDate;
    private SupplierResponseDTO supplier;
    private SiteDTO site; // Embed SiteDTO
    private String billImagePath;
    private String overallGrnStatus; // String representation of the enum
    private boolean grnHardcopyReceivedByPurchaser;
    private boolean grnHardcopyHandedToAccountant;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<BillItemResponseDTO> billItems;
}
