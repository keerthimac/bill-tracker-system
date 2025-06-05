package com.keerthimac.bill_tracker_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MasterMaterialRequestDTO {

    @Size(max = 50, message = "Material code cannot exceed 50 characters")
    private String materialCode; // Optional, but if provided, might have uniqueness handled by service

    @NotBlank(message = "Material name cannot be blank")
    @Size(max = 255, message = "Material name cannot exceed 255 characters")
    private String name;

    private String description; // Optional

    @NotBlank(message = "Default unit cannot be blank")
    @Size(max = 20, message = "Default unit cannot exceed 20 characters")
    private String defaultUnit;

    @NotNull(message = "Item Category ID cannot be null")
    private Long itemCategoryId; // ID of the existing ItemCategory to link to
}
