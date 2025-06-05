package com.keerthimac.bill_tracker_system.controller;

import com.keerthimac.bill_tracker_system.dto.PriceRevisionLogDTO;
import com.keerthimac.bill_tracker_system.dto.SupplierMaterialPriceRequestDTO;
import com.keerthimac.bill_tracker_system.dto.SupplierMaterialPriceResponseDTO;
import com.keerthimac.bill_tracker_system.service.SupplierMaterialPriceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/supplier-prices")
public class SupplierMaterialPriceController {

    private final SupplierMaterialPriceService supplierMaterialPriceService;
    private static final String DEFAULT_USER = "API_USER"; // Placeholder for actual logged-in user

    @Autowired
    public SupplierMaterialPriceController(SupplierMaterialPriceService supplierMaterialPriceService) {
        this.supplierMaterialPriceService = supplierMaterialPriceService;
    }

    // Add a new price entry for a supplier's material
    @PostMapping
    public ResponseEntity<SupplierMaterialPriceResponseDTO> addPrice(
            @Valid @RequestBody SupplierMaterialPriceRequestDTO requestDTO) {
        // In a real app, 'changedByUser' would come from the security context (logged-in user)
        SupplierMaterialPriceResponseDTO createdPrice = supplierMaterialPriceService.addPrice(requestDTO, DEFAULT_USER);
        return new ResponseEntity<>(createdPrice, HttpStatus.CREATED);
    }

    // Update an existing price entry
    @PutMapping("/{priceId}")
    public ResponseEntity<SupplierMaterialPriceResponseDTO> updatePrice(
            @PathVariable Long priceId,
            @Valid @RequestBody SupplierMaterialPriceRequestDTO requestDTO) {
        SupplierMaterialPriceResponseDTO updatedPrice = supplierMaterialPriceService.updatePrice(priceId, requestDTO, DEFAULT_USER);
        return ResponseEntity.ok(updatedPrice);
    }

    // Get a specific price entry by its ID
    @GetMapping("/{priceId}")
    public ResponseEntity<SupplierMaterialPriceResponseDTO> getPriceById(@PathVariable Long priceId) {
        Optional<SupplierMaterialPriceResponseDTO> priceDTO = supplierMaterialPriceService.getPriceById(priceId);
        return priceDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get all price entries for a specific supplier
    @GetMapping("/by-supplier/{supplierId}")
    public ResponseEntity<List<SupplierMaterialPriceResponseDTO>> getPricesBySupplier(@PathVariable Long supplierId) {
        List<SupplierMaterialPriceResponseDTO> prices = supplierMaterialPriceService.getPricesBySupplierId(supplierId);
        if (prices.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(prices);
    }

    // Get all price entries for a specific master material
    @GetMapping("/by-material/{masterMaterialId}")
    public ResponseEntity<List<SupplierMaterialPriceResponseDTO>> getPricesByMasterMaterial(@PathVariable Long masterMaterialId) {
        List<SupplierMaterialPriceResponseDTO> prices = supplierMaterialPriceService.getPricesByMasterMaterialId(masterMaterialId);
        if (prices.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(prices);
    }

    // Get all price entries for a specific supplier and master material combination
    @GetMapping("/by-supplier-material")
    public ResponseEntity<List<SupplierMaterialPriceResponseDTO>> getPricesBySupplierAndMasterMaterial(
            @RequestParam Long supplierId,
            @RequestParam Long masterMaterialId) {
        List<SupplierMaterialPriceResponseDTO> prices = supplierMaterialPriceService.getPricesBySupplierAndMasterMaterial(supplierId, masterMaterialId);
        if (prices.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(prices);
    }

    // Get the active price for a supplier, material, unit on a specific date
    @GetMapping("/active-price")
    public ResponseEntity<SupplierMaterialPriceResponseDTO> getActivePrice(
            @RequestParam Long supplierId,
            @RequestParam Long masterMaterialId,
            @RequestParam String unit,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Optional<SupplierMaterialPriceResponseDTO> activePrice = supplierMaterialPriceService.getActivePriceForSupplierMaterialUnit(
                supplierId, masterMaterialId, unit, date);
        return activePrice.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build()); // 404 if no active price found
    }

    // Deactivate a price entry (soft delete)
    @PatchMapping("/{priceId}/deactivate")
    public ResponseEntity<Void> deactivatePrice(@PathVariable Long priceId) {
        supplierMaterialPriceService.deactivatePrice(priceId, DEFAULT_USER);
        return ResponseEntity.noContent().build(); // Or return the updated DTO if service returns it
    }

    // Get revision history for a specific price entry
    @GetMapping("/{priceId}/history")
    public ResponseEntity<List<PriceRevisionLogDTO>> getPriceRevisionHistory(@PathVariable Long priceId) {
        // The service method currently takes supplierMaterialPriceId.
        // This controller endpoint uses {priceId} which is the SupplierMaterialPrice ID.
        List<PriceRevisionLogDTO> history = supplierMaterialPriceService.getPriceRevisionHistory(priceId);
        if (history.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(history);
    }
}