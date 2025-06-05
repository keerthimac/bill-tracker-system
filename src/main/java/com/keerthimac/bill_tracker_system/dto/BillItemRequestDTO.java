package com.keerthimac.bill_tracker_system.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillItemRequestDTO {

    @NotNull(message = "Master Material ID cannot be null for a bill item")
    private Long masterMaterialId; // Changed from materialName and itemCategoryId

    @NotNull(message = "Quantity cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Quantity must be greater than zero")
    private BigDecimal quantity;

    @NotBlank(message = "Unit cannot be blank")
    @Size(max = 20, message = "Unit cannot exceed 20 characters")
    private String unit;

    @NotNull(message = "Unit price cannot be null")
    @DecimalMin(value = "0.0", message = "Unit price cannot be negative") // Allows zero, change if needed
    private BigDecimal unitPrice;

    // Remarks are optional for creation, can be added during GRN update
    // private String remarks;
}