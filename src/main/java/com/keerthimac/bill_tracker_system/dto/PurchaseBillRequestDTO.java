package com.keerthimac.bill_tracker_system.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseBillRequestDTO {

    @NotBlank(message = "Bill number cannot be blank")
    private String billNumber;

    @NotNull(message = "Bill date cannot be null")
    private LocalDate billDate;

    @NotNull(message = "Supplier ID cannot be null")
    private Long supplierId;

    @NotNull(message = "Site ID cannot be null")
    private Long siteId;

    // billImagePath will be handled separately

    @NotEmpty(message = "A bill must have at least one item.")
    @Valid // Ensures validation rules in BillItemRequestDTO are checked
    private List<BillItemRequestDTO> items; // This list now contains the updated BillItemRequestDTO
}