package com.keerthimac.bill_tracker_system.service.impl;

import com.keerthimac.bill_tracker_system.dto.SupplierRequestDTO;
import com.keerthimac.bill_tracker_system.dto.SupplierResponseDTO;
import com.keerthimac.bill_tracker_system.entity.Supplier;
import com.keerthimac.bill_tracker_system.exception.DuplicateResourceException;
import com.keerthimac.bill_tracker_system.exception.ResourceNotFoundException;
import com.keerthimac.bill_tracker_system.mapper.SupplierMapper;
import com.keerthimac.bill_tracker_system.repository.SupplierRepository;
import com.keerthimac.bill_tracker_system.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service // Marks this as a Spring service component
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    @Autowired
    public SupplierServiceImpl(SupplierRepository supplierRepository, SupplierMapper supplierMapper) {
        this.supplierRepository = supplierRepository;
        this.supplierMapper = supplierMapper;
    }

    @Override
    @Transactional // Ensures the operation is atomic
    public SupplierResponseDTO createSupplier(SupplierRequestDTO supplierRequestDTO) {
        // Optional: Check if a supplier with the same name already exists
        supplierRepository.findByNameIgnoreCase(supplierRequestDTO.getName()).ifPresent(s -> {
            throw new DuplicateResourceException("Supplier with name '" + supplierRequestDTO.getName() + "' already exists.");
        });
        // Optional: Check for duplicate email if email should be unique
        if (supplierRequestDTO.getEmail() != null && !supplierRequestDTO.getEmail().isEmpty()) {
            supplierRepository.findByEmailIgnoreCase(supplierRequestDTO.getEmail()).ifPresent(s -> {
                throw new DuplicateResourceException("Supplier with email '" + supplierRequestDTO.getEmail() + "' already exists.");
            });
        }


        Supplier supplier = supplierMapper.toEntity(supplierRequestDTO);
        Supplier savedSupplier = supplierRepository.save(supplier);
        return supplierMapper.toDto(savedSupplier);
    }

    @Override
    @Transactional(readOnly = true) // Optimization for read operations
    public Optional<SupplierResponseDTO> getSupplierById(Long supplierId) {
        return supplierRepository.findById(supplierId)
                .map(supplierMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponseDTO> getAllSuppliers() {
        List<Supplier> suppliers = supplierRepository.findAll();
        return supplierMapper.toDtoList(suppliers);
    }

    @Override
    @Transactional
    public SupplierResponseDTO updateSupplier(Long supplierId, SupplierRequestDTO supplierRequestDTO) {
        Supplier existingSupplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + supplierId));

        // Optional: Check if the new name conflicts with another existing supplier
        if (!existingSupplier.getName().equalsIgnoreCase(supplierRequestDTO.getName())) {
            supplierRepository.findByNameIgnoreCase(supplierRequestDTO.getName()).ifPresent(s -> {
                if (!s.getId().equals(supplierId)) { // If it's a different supplier
                    throw new DuplicateResourceException("Another supplier with name '" + supplierRequestDTO.getName() + "' already exists.");
                }
            });
        }
        // Optional: Check for duplicate email if email should be unique and is being changed
        if (supplierRequestDTO.getEmail() != null && !supplierRequestDTO.getEmail().isEmpty() &&
                (existingSupplier.getEmail() == null || !existingSupplier.getEmail().equalsIgnoreCase(supplierRequestDTO.getEmail())) ) {
            supplierRepository.findByEmailIgnoreCase(supplierRequestDTO.getEmail()).ifPresent(s -> {
                if(!s.getId().equals(supplierId)) {
                    throw new DuplicateResourceException("Another supplier with email '" + supplierRequestDTO.getEmail() + "' already exists.");
                }
            });
        }


        // Update fields from DTO (MapStruct can also do this with @MappingTarget)
        existingSupplier.setName(supplierRequestDTO.getName());
        existingSupplier.setContactPerson(supplierRequestDTO.getContactPerson());
        existingSupplier.setContactNumber(supplierRequestDTO.getContactNumber());
        existingSupplier.setEmail(supplierRequestDTO.getEmail());
        existingSupplier.setAddress(supplierRequestDTO.getAddress());

        Supplier updatedSupplier = supplierRepository.save(existingSupplier);
        return supplierMapper.toDto(updatedSupplier);
    }

    @Override
    @Transactional
    public void deleteSupplier(Long supplierId) {
        if (!supplierRepository.existsById(supplierId)) {
            throw new ResourceNotFoundException("Supplier not found with id: " + supplierId);
        }
        // Optional: Add logic here to check if the supplier is associated with any PurchaseBills
        // If so, you might prevent deletion or handle it differently (e.g., soft delete, disassociate)
        // For now, we'll proceed with hard deletion.
        supplierRepository.deleteById(supplierId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SupplierResponseDTO> findSupplierByName(String name) {
        return supplierRepository.findByNameIgnoreCase(name)
                .map(supplierMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponseDTO> searchSuppliersByName(String nameFragment) {
        List<Supplier> suppliers = supplierRepository.findByNameContainingIgnoreCase(nameFragment);
        return supplierMapper.toDtoList(suppliers);
    }
}