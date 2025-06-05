package com.keerthimac.bill_tracker_system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
// Example of a table-level unique constraint: a supplier shouldn't have multiple identical active price entries
// for the same material, unit, and overlapping effective date. This can be complex.
// A simpler approach might be a unique constraint on (supplier_id, master_material_id, unit, effective_from_date)
// if you don't allow multiple prices for the same material from the same supplier on the same day for the same unit.
// For now, we'll keep it simple and rely on service-level logic for complex validation,
// but @Table(uniqueConstraints = {...}) is where you'd define DB level ones.
@Table(name = "supplier_material_prices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierMaterialPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_material_id", nullable = false)
    private MasterMaterial masterMaterial;

    @Column(name = "price", nullable = false, precision = 19, scale = 4) // Adjust precision and scale as needed for your currency
    private BigDecimal price;

    @Column(name = "unit", nullable = false, length = 20) // e.g., "BAGS", "KG", "PCS". Should align with units for the MasterMaterial.
    private String unit;

    @Column(name = "effective_from_date", nullable = false)
    private LocalDate effectiveFromDate;

    @Column(name = "effective_to_date", nullable = true) // Can be null if the price is indefinitely active
    private LocalDate effectiveToDate;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true; // Default to active

    @CreationTimestamp // Automatically set on creation
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp // Automatically set on update
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Consider adding a field for 'remarks' or 'notes' if needed
}
