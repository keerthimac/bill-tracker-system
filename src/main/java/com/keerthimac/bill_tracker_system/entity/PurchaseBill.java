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
import java.util.List;

@Entity
@Table(name = "purchase_bills")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseBill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String billNumber;

    @Column(nullable = false)
    private LocalDate billDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id") // You can make this nullable=false if a supplier is mandatory
    private Supplier supplier; // <<< ADD THIS LINE

    @ManyToOne(fetch = FetchType.LAZY) // LAZY fetching is generally preferred for performance
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    private String billImagePath; // Path to the uploaded image

    @Enumerated(EnumType.STRING)
    private OverallGrnStatus overallGrnStatus = OverallGrnStatus.PENDING;

    private boolean grnHardcopyReceivedByPurchaser = false;
    private boolean grnHardcopyHandedToAccountant = false;

    @Column(precision = 10, scale = 2) // Example precision: 10 digits, 2 decimal places
    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "purchaseBill", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BillItem> billItems;

    @CreationTimestamp // Automatically set on creation
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp // Automatically set on update
    private LocalDateTime updatedAt;
}
