package com.keerthimac.bill_tracker_system.controller;

import com.keerthimac.bill_tracker_system.dto.SupplierRequestDTO;
import com.keerthimac.bill_tracker_system.dto.SupplierResponseDTO;
import com.keerthimac.bill_tracker_system.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/suppliers") // Base path for all supplier-related APIs
public class SupplierController {

    private final SupplierService supplierService;

    @Autowired
    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    // POST: Create a new Supplier
    @PostMapping
    public ResponseEntity<SupplierResponseDTO> createSupplier(@Valid @RequestBody SupplierRequestDTO supplierRequestDTO) {
        // The service layer handles checking for duplicates (e.g., name, email)
        SupplierResponseDTO createdSupplier = supplierService.createSupplier(supplierRequestDTO);
        return new ResponseEntity<>(createdSupplier, HttpStatus.CREATED);
    }

    // GET: Retrieve a Supplier by its ID
    @GetMapping("/{id}")
    public ResponseEntity<SupplierResponseDTO> getSupplierById(@PathVariable Long id) {
        Optional<SupplierResponseDTO> supplierDTO = supplierService.getSupplierById(id);
        return supplierDTO.map(ResponseEntity::ok) // If present, return 200 OK
                .orElseGet(() -> ResponseEntity.notFound().build()); // If not, return 404 Not Found
    }

    // GET: Retrieve all Suppliers or search by name fragment
    @GetMapping
    public ResponseEntity<List<SupplierResponseDTO>> getAllOrSearchSuppliers(
            @RequestParam(name = "search", required = false) String nameFragment) {
        List<SupplierResponseDTO> suppliers;
        if (nameFragment != null && !nameFragment.trim().isEmpty()) {
            suppliers = supplierService.searchSuppliersByName(nameFragment);
        } else {
            suppliers = supplierService.getAllSuppliers();
        }

        if (suppliers.isEmpty()) {
            return ResponseEntity.noContent().build(); // Return 204 No Content if list is empty
        }
        return ResponseEntity.ok(suppliers);
    }

    // PUT: Update an existing Supplier
    @PutMapping("/{id}")
    public ResponseEntity<SupplierResponseDTO> updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody SupplierRequestDTO supplierRequestDTO) {
        // The service layer handles checking if supplier exists and potential duplicate name/email on update
        SupplierResponseDTO updatedSupplier = supplierService.updateSupplier(id, supplierRequestDTO);
        return ResponseEntity.ok(updatedSupplier);
        // Note: If updateSupplier throws ResourceNotFoundException,
        // a @ControllerAdvice or @ResponseStatus on the exception would handle the 404.
    }

    // DELETE: Delete a Supplier by its ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        // The service layer handles checking if supplier exists
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build(); // Return 204 No Content on successful deletion
    }

    // GET: Specific endpoint to find a supplier by exact name (case-insensitive)
    // This is an alternative to using the search parameter in getAllOrSearchSuppliers
    // if you need a dedicated endpoint for exact name lookup.
    @GetMapping("/by-name")
    public ResponseEntity<SupplierResponseDTO> getSupplierByName(@RequestParam String name) {
        Optional<SupplierResponseDTO> supplierDTO = supplierService.findSupplierByName(name);
        return supplierDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

