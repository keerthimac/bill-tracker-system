package com.keerthimac.bill_tracker_system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "bill_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_bill_id", nullable = false)
    private PurchaseBill purchaseBill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_material_id", nullable = false)
    private MasterMaterial masterMaterial; // Links to MasterMaterial

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal quantity;

    @Column(nullable = false, length = 20)
    private String unit;

    @Column(name = "unit_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal unitPrice;

    @Column(name = "item_total_price", precision = 19, scale = 4)
    private BigDecimal itemTotalPrice;

    @Column(name = "grn_received_for_item", nullable = false)
    private boolean grnReceivedForItem = false;

    @Lob
    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @PrePersist
    @PreUpdate
    public void calculateItemTotalPrice() {
        if (this.quantity != null && this.unitPrice != null) {
            this.itemTotalPrice = this.quantity.multiply(this.unitPrice);
        } else {
            this.itemTotalPrice = BigDecimal.ZERO;
        }
    }
}