package com.keerthimac.bill_tracker_system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SupplierRequestDTO {

    @NotBlank(message = "Supplier name cannot be blank")
    @Size(max = 255, message = "Supplier name cannot exceed 255 characters")
    private String name;

    @Size(max = 255, message = "Contact person name cannot exceed 255 characters")
    private String contactPerson;

    @Size(max = 20, message = "Contact number cannot exceed 20 characters") // Adjust size as needed
    private String contactNumber;

    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;

    private String address; // No specific validation for now, can add later
}
