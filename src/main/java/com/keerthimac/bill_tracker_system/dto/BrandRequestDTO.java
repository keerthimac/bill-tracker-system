package com.keerthimac.bill_tracker_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
// Consider adding URL validation if this field must be a URL
// import org.hibernate.validator.constraints.URL; // Example
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandRequestDTO {

    @NotBlank(message = "Brand name cannot be blank")
    @Size(max = 100, message = "Brand name cannot exceed 100 characters")
    private String name;

    private String description; // Optional

    @Size(max = 2048, message = "Brand image path/URL cannot exceed 2048 characters")
    // @URL(message = "Brand image path must be a valid URL") // Optional: if you want to enforce URL format
    private String brandImagePath; // <<< NEW FIELD (optional in request)
}