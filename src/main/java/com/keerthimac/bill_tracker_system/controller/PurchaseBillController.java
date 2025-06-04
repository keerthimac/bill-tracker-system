package com.keerthimac.bill_tracker_system.controller;

import com.keerthimac.bill_tracker_system.dto.PurchaseBillRequestDTO;
import com.keerthimac.bill_tracker_system.dto.PurchaseBillResponseDTO;
import com.keerthimac.bill_tracker_system.service.PurchaseBillService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat; // For date parsing
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// import org.springframework.web.multipart.MultipartFile; // Keep for future file upload

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/purchase-bills")
public class PurchaseBillController {

    private final PurchaseBillService purchaseBillService;

    @Autowired
    public PurchaseBillController(PurchaseBillService purchaseBillService) {
        this.purchaseBillService = purchaseBillService;
    }

    // POST: Create a new Purchase Bill
    // No change in method signature needed.
    // The @Valid @RequestBody PurchaseBillRequestDTO already reflects the need for supplierId.
    // Client must now send: { ..., "supplierId": 123, ... }
    @PostMapping
    public ResponseEntity<PurchaseBillResponseDTO> createPurchaseBill(@Valid @RequestBody PurchaseBillRequestDTO billRequestDTO) {
        PurchaseBillResponseDTO createdBill = purchaseBillService.createPurchaseBill(billRequestDTO);
        return new ResponseEntity<>(createdBill, HttpStatus.CREATED);
    }

    // GET: Retrieve a Purchase Bill by its ID
    // No change in method signature needed.
    // The PurchaseBillResponseDTO will automatically include the nested SupplierResponseDTO.
    // Response will be like: { ..., "supplier": {"id": 123, "name": "Supplier Name", ...}, ...}
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseBillResponseDTO> getPurchaseBillById(@PathVariable Long id) {
        Optional<PurchaseBillResponseDTO> billDTO = purchaseBillService.getPurchaseBillById(id);
        return billDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET: Retrieve all Purchase Bills with optional filtering
    // No change in method signature needed for supplier integration.
    // The list will contain PurchaseBillResponseDTOs with nested SupplierResponseDTO.
    @GetMapping
    public ResponseEntity<List<PurchaseBillResponseDTO>> getAllPurchaseBills(
            @RequestParam(required = false) Long siteId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<PurchaseBillResponseDTO> bills;
        if (siteId != null && startDate != null && endDate != null) {
            bills = purchaseBillService.getPurchaseBillsBySiteAndDateRange(siteId, startDate, endDate);
        } else if (siteId != null) {
            bills = purchaseBillService.getPurchaseBillsBySite(siteId);
        } else if (startDate != null && endDate != null) {
            bills = purchaseBillService.getPurchaseBillsByDateRange(startDate, endDate);
        } else {
            bills = purchaseBillService.getAllPurchaseBills();
        }

        if (bills.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(bills);
    }

    // PUT: Update general details of a Purchase Bill
    // No change in method signature needed.
    // The @Valid @RequestBody PurchaseBillRequestDTO already reflects the need for supplierId if updating supplier link.
    // Client must now send: { ..., "supplierId": 123, ... } if changing the supplier.
    @PutMapping("/{billId}")
    public ResponseEntity<PurchaseBillResponseDTO> updatePurchaseBillDetails(
            @PathVariable Long billId,
            @Valid @RequestBody PurchaseBillRequestDTO billRequestDTO) {
        PurchaseBillResponseDTO updatedBill = purchaseBillService.updatePurchaseBillDetails(billId, billRequestDTO);
        return ResponseEntity.ok(updatedBill);
    }

    // PATCH or PUT: Update GRN status for a specific Bill Item
    // No direct impact from supplier integration on this endpoint's signature or core logic.
    // The response PurchaseBillResponseDTO will naturally include the updated supplier info.
    @PatchMapping("/items/{billItemId}/grn")
    public ResponseEntity<PurchaseBillResponseDTO> updateGrnForItem(
            @PathVariable Long billItemId,
            @RequestParam boolean received,
            @RequestParam(required = false) String remarks) {
        PurchaseBillResponseDTO updatedBill = purchaseBillService.updateGrnReceivedForItem(billItemId, received, remarks);
        return ResponseEntity.ok(updatedBill);
    }

    // PATCH or PUT: Update GRN hardcopy status for a Purchase Bill
    // No direct impact from supplier integration.
    @PatchMapping("/{billId}/grn-hardcopy")
    public ResponseEntity<PurchaseBillResponseDTO> updateGrnHardcopyStatus(
            @PathVariable Long billId,
            @RequestParam boolean receivedByPurchaser,
            @RequestParam boolean handedToAccountant) {
        PurchaseBillResponseDTO updatedBill = purchaseBillService.updateGrnHardcopyStatus(billId, receivedByPurchaser, handedToAccountant);
        return ResponseEntity.ok(updatedBill);
    }

    // POST: Endpoint for uploading a bill image
    // No direct impact from supplier integration.
    @PostMapping("/{billId}/upload-image")
    public ResponseEntity<PurchaseBillResponseDTO> uploadBillImage(
            @PathVariable Long billId,
            @RequestParam("file") /* MultipartFile file */ String tempFilePathPlaceholder
    ) {
        // Assuming tempFilePathPlaceholder is a temporary stand-in for actual file path from a FileStorageService
        PurchaseBillResponseDTO updatedBill = purchaseBillService.updateBillImagePath(billId, tempFilePathPlaceholder);
        return ResponseEntity.ok(updatedBill);
    }

    // DELETE: Delete a Purchase Bill
    // No direct impact from supplier integration.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePurchaseBill(@PathVariable Long id) {
        purchaseBillService.deletePurchaseBill(id);
        return ResponseEntity.noContent().build();
    }
}