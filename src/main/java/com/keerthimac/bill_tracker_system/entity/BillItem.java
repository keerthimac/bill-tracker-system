package com.keerthimac.bill_tracker_system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "bill_items") // This is the table Hibernate says doesn't exist for the DROP FK
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_bill_id", nullable = false)
    private PurchaseBill purchaseBill; // Foreign key to purchase_bills table

    @Column(nullable = false)
    private String materialName; // As per our current Phase 1 design

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_category_id", nullable = false)
    private ItemCategory itemCategory; // Foreign key to item_categories table

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal quantity;

    @Column(nullable = false)
    private String unit;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal itemTotalPrice;

    private boolean grnReceivedForItem = false;

    @Column(columnDefinition = "TEXT")
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