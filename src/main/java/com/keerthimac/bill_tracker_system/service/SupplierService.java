package com.keerthimac.bill_tracker_system.service;

import com.keerthimac.bill_tracker_system.dto.SupplierRequestDTO;
import com.keerthimac.bill_tracker_system.dto.SupplierResponseDTO;

import java.util.List;
import java.util.Optional;

public interface SupplierService {

    /**
     * Creates a new supplier.
     * @param supplierRequestDTO DTO containing supplier details.
     * @return DTO of the created supplier.
     * @throws // Add specific exceptions like SupplierAlreadyExistsException if needed
     */
    SupplierResponseDTO createSupplier(SupplierRequestDTO supplierRequestDTO);

    /**
     * Retrieves a supplier by its ID.
     * @param supplierId The ID of the supplier to retrieve.
     * @return An Optional containing the supplier DTO if found, or an empty Optional otherwise.
     */
    Optional<SupplierResponseDTO> getSupplierById(Long supplierId);

    /**
     * Retrieves all suppliers.
     * @return A list of all supplier DTOs.
     */
    List<SupplierResponseDTO> getAllSuppliers();

    /**
     * Updates an existing supplier.
     * @param supplierId The ID of the supplier to update.
     * @param supplierRequestDTO DTO containing updated supplier details.
     * @return DTO of the updated supplier.
     * @throws // Add specific exceptions like SupplierNotFoundException if needed
     */
    SupplierResponseDTO updateSupplier(Long supplierId, SupplierRequestDTO supplierRequestDTO);

    /**
     * Deletes a supplier by its ID.
     * @param supplierId The ID of the supplier to delete.
     * @throws // Add specific exceptions like SupplierNotFoundException or SupplierInUseException if needed
     */
    void deleteSupplier(Long supplierId);

    /**
     * Finds a supplier by its name (case-insensitive).
     * @param name The name of the supplier to find.
     * @return An Optional containing the supplier DTO if found.
     */
    Optional<SupplierResponseDTO> findSupplierByName(String name);

    /**
     * Finds suppliers whose names contain the given fragment (case-insensitive).
     * @param nameFragment The fragment to search for in supplier names.
     * @return A list of matching supplier DTOs.
     */
    List<SupplierResponseDTO> searchSuppliersByName(String nameFragment);
}
