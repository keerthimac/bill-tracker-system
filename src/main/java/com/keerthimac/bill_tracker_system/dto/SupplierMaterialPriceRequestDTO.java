package com.keerthimac.bill_tracker_system.dto; // Your DTO package

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierMaterialPriceRequestDTO {

    @NotNull(message = "Supplier ID cannot be null")
    private Long supplierId;

    @NotNull(message = "Master Material ID cannot be null")
    private Long masterMaterialId;

    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero") // Or 0.0 inclusive if zero price allowed
    private BigDecimal price;

    @NotBlank(message = "Unit cannot be blank")
    @Size(max = 20, message = "Unit cannot exceed 20 characters")
    private String unit;

    @NotNull(message = "Effective from date cannot be null")
    // @FutureOrPresent(message = "Effective from date must be today or a future date") // Use if new prices cannot be backdated
    private LocalDate effectiveFromDate;

    private LocalDate effectiveToDate; // Optional, can be null

    // isActive is usually managed by the system based on effective dates or explicitly.
    // For a request, we might not always include it, or default it.
    // If included, it might be used to explicitly deactivate a price.
    private Boolean isActive; // Use Boolean to allow null if not specified, service can default to true
}