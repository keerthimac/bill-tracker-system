package com.keerthimac.bill_tracker_system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "price_revision_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceRevisionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to the specific SupplierMaterialPrice entry that was changed.
    // If SupplierMaterialPrice entries can be deleted, consider making this optional
    // or storing just the IDs of supplier and material. For now, assuming SMP entries are deactivated, not deleted.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_material_price_id", nullable = false)
    private SupplierMaterialPrice supplierMaterialPrice;

    @Column(name = "old_price", nullable = true, precision = 19, scale = 4) // Nullable if it's the initial price setting
    private BigDecimal oldPrice;

    @Column(name = "new_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal newPrice;

    @Column(name = "old_effective_from_date", nullable = true) // Nullable for initial price setting
    private LocalDate oldEffectiveFromDate;

    @Column(name = "new_effective_from_date", nullable = false)
    private LocalDate newEffectiveFromDate;

    @Column(name = "old_effective_to_date", nullable = true)
    private LocalDate oldEffectiveToDate;

    @Column(name = "new_effective_to_date", nullable = true)
    private LocalDate newEffectiveToDate;

    // We could also log changes to 'unit' or 'isActive' status if needed

    @CreationTimestamp // This log entry is created at the time of the change
    @Column(name = "change_timestamp", nullable = false, updatable = false)
    private LocalDateTime changeTimestamp;

    @Column(name = "changed_by_user", length = 100) // Placeholder for user ID or name
    private String changedByUser; // This would typically be populated with the logged-in user's identifier

    @Lob
    @Column(name = "reason_for_change", columnDefinition = "TEXT")
    private String reasonForChange; // Optional field for noting why the price was changed
}