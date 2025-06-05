package com.keerthimac.bill_tracker_system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "master_materials")
@Data // Lombok: Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
public class MasterMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment primary key
    private Long id;

    @Column(name = "material_code", unique = true, nullable = true, length = 50) // Optional, but if used, should be unique
    private String materialCode;

    @Column(name = "name", nullable = false, length = 255) // Material name is mandatory
    private String name;

    @Lob // For potentially longer text, maps to CLOB or TEXT depending on DB
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "default_unit", nullable = false, length = 20) // e.g., "BAGS", "KG", "TONS", "PCS", "MTR"
    private String defaultUnit;

    @ManyToOne(fetch = FetchType.LAZY) // Many master materials can belong to one item category
    @JoinColumn(name = "item_category_id", nullable = false) // Foreign key to item_categories table
    private ItemCategory itemCategory; // Link to your existing ItemCategory entity

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = true) // This is the DB column name
    private Brand brand;

    // Consider adding audit fields if needed (createdAt, updatedAt)
     @CreationTimestamp
     private LocalDateTime createdAt;
     @UpdateTimestamp
     private LocalDateTime updatedAt;
}
