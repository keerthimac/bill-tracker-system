package com.keerthimac.bill_tracker_system.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class PurchaseBillRequestDTO {
    private String billNumber;
    private LocalDate billDate;
    private String supplierName;
    private Long siteId; // ID of the Site
    // billImagePath will be handled separately, likely via a multipart file upload
    private List<BillItemRequestDTO> items;
    // grn statuses and totalAmount are usually not set by the client on creation
}
