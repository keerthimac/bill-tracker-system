package com.keerthimac.bill_tracker_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SiteDTO {
    private Long id; // Not part of request for creation, but used in response & path for update/delete

    @NotBlank(message = "Site name cannot be blank")
    @Size(max = 255, message = "Site name must be less than 255 characters")
    private String name;

    @Size(max = 255, message = "Site location must be less than 255 characters")
    private String location; // Optional field, so no @NotBlank
}