package com.keerthimac.bill_tracker_system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "item_categories")
@Data // Lombok: Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
public class ItemCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment for most databases
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // e.g., "Raw Materials", "Tools"
}