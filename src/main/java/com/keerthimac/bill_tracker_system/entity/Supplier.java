package com.keerthimac.bill_tracker_system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "suppliers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) // Assuming supplier name should be unique
    private String name;

    private String contactPerson;

    private String contactNumber;

    private String email;

    @Column(columnDefinition = "TEXT")
    private String address;

    // We can add more fields later like VAT/TIN number, etc. if needed
    // For Phase 2 (pricing), this entity would be linked to SupplierPriceListItems
}
