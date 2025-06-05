package com.keerthimac.bill_tracker_system.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImagePathUpdateRequestDTO {

    @Size(max = 2048, message = "Image path/URL cannot exceed 2048 characters")
    // You could add @URL validation here if the path must always be a URL
    private String imagePath;
}