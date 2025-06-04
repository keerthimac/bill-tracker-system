package com.keerthimac.bill_tracker_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ItemCategoryDTO {
    private Long id; // Not part of request for creation, but used in response & path for update/delete

    @NotBlank(message = "Item category name cannot be blank")
    @Size(max = 100, message = "Item category name must be less than 100 characters") // Adjusted size
    private String name;
}
