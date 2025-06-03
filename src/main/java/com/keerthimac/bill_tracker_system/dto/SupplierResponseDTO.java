package com.keerthimac.bill_tracker_system.dto;

import lombok.Data;

@Data
public class SupplierResponseDTO {
    private Long id;
    private String name;
    private String contactPerson;
    private String contactNumber;
    private String email;
    private String address;
}
