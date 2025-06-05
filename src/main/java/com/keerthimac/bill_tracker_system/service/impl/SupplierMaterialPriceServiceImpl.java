package com.keerthimac.bill_tracker_system.service.impl;

import com.keerthimac.bill_tracker_system.dto.PriceRevisionLogDTO;
import com.keerthimac.bill_tracker_system.dto.SupplierMaterialPriceRequestDTO;
import com.keerthimac.bill_tracker_system.dto.SupplierMaterialPriceResponseDTO;
import com.keerthimac.bill_tracker_system.exception.InvalidDataException;
import com.keerthimac.bill_tracker_system.exception.ResourceNotFoundException;
import com.keerthimac.bill_tracker_system.mapper.PriceRevisionLogMapper;
import com.keerthimac.bill_tracker_system.mapper.SupplierMaterialPriceMapper;
import com.keerthimac.bill_tracker_system.entity.*; // Supplier, MasterMaterial, SupplierMaterialPrice, PriceRevisionLog
import com.keerthimac.bill_tracker_system.repository.*; // All needed repositories
import com.keerthimac.bill_tracker_system.service.SupplierMaterialPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class SupplierMaterialPriceServiceImpl implements SupplierMaterialPriceService {

    private final SupplierMaterialPriceRepository supplierMaterialPriceRepository;
    private final SupplierRepository supplierRepository;
    private final MasterMaterialRepository masterMaterialRepository;
    private final PriceRevisionLogRepository priceRevisionLogRepository;
    private final SupplierMaterialPriceMapper supplierMaterialPriceMapper;
    private final PriceRevisionLogMapper priceRevisionLogMapper;

    @Autowired
    public SupplierMaterialPriceServiceImpl(
            SupplierMaterialPriceRepository supplierMaterialPriceRepository,
            SupplierRepository supplierRepository,
            MasterMaterialRepository masterMaterialRepository,
            PriceRevisionLogRepository priceRevisionLogRepository,
            SupplierMaterialPriceMapper supplierMaterialPriceMapper,
            PriceRevisionLogMapper priceRevisionLogMapper) {
        this.supplierMaterialPriceRepository = supplierMaterialPriceRepository;
        this.supplierRepository = supplierRepository;
        this.masterMaterialRepository = masterMaterialRepository;
        this.priceRevisionLogRepository = priceRevisionLogRepository;
        this.supplierMaterialPriceMapper = supplierMaterialPriceMapper;
        this.priceRevisionLogMapper = priceRevisionLogMapper;
    }

    private void validateAndSetSupplierAndMaterial(SupplierMaterialPriceRequestDTO requestDTO, SupplierMaterialPrice entity) {
        Supplier supplier = supplierRepository.findById(requestDTO.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + requestDTO.getSupplierId()));
        MasterMaterial masterMaterial = masterMaterialRepository.findById(requestDTO.getMasterMaterialId())
                .orElseThrow(() -> new ResourceNotFoundException("MasterMaterial not found with id: " + requestDTO.getMasterMaterialId()));
        entity.setSupplier(supplier);
        entity.setMasterMaterial(masterMaterial);
    }

    private void checkForOverlappingPrices(SupplierMaterialPriceRequestDTO requestDTO, Long excludeIdIfUpdate) {
        if (requestDTO.getEffectiveToDate() != null && requestDTO.getEffectiveFromDate().isAfter(requestDTO.getEffectiveToDate())) {
            throw new InvalidDataException("Effective from date cannot be after effective to date.");
        }

        List<SupplierMaterialPrice> overlaps = supplierMaterialPriceRepository.findOverlappingPrices(
                requestDTO.getSupplierId(),
                requestDTO.getMasterMaterialId(),
                requestDTO.getUnit(),
                requestDTO.getEffectiveFromDate(),
                requestDTO.getEffectiveToDate(),
                excludeIdIfUpdate // null for new entries, ID for updates
        );
        if (!overlaps.isEmpty()) {
            throw new InvalidDataException("The provided price entry dates overlap with an existing active price for this supplier, material, and unit.");
        }
    }


    private void logPriceChange(SupplierMaterialPrice priceEntry,
                                SupplierMaterialPrice oldPriceEntryIfUpdate, // null for new entries
                                String changedByUser, String reason) {
        PriceRevisionLog log = new PriceRevisionLog();
        log.setSupplierMaterialPrice(priceEntry);
        log.setNewPrice(priceEntry.getPrice());
        log.setNewEffectiveFromDate(priceEntry.getEffectiveFromDate());
        log.setNewEffectiveToDate(priceEntry.getEffectiveToDate());

        if (oldPriceEntryIfUpdate != null) {
            log.setOldPrice(oldPriceEntryIfUpdate.getPrice());
            log.setOldEffectiveFromDate(oldPriceEntryIfUpdate.getEffectiveFromDate());
            log.setOldEffectiveToDate(oldPriceEntryIfUpdate.getEffectiveToDate());
        }
        // changeTimestamp is auto by @CreationTimestamp
        log.setChangedByUser(changedByUser); // In a real app, get from security context
        log.setReasonForChange(reason); // Could be passed in or set based on context
        priceRevisionLogRepository.save(log);
    }


    @Override
    @Transactional
    public SupplierMaterialPriceResponseDTO addPrice(SupplierMaterialPriceRequestDTO requestDTO, String changedByUser) {
        checkForOverlappingPrices(requestDTO, null); // No ID to exclude for new price

        SupplierMaterialPrice newPriceEntry = supplierMaterialPriceMapper.toEntity(requestDTO);
        validateAndSetSupplierAndMaterial(requestDTO, newPriceEntry);

        if (requestDTO.getIsActive() == null) { // Default to true if not specified in request
            newPriceEntry.setActive(true);
        } else {
            newPriceEntry.setActive(requestDTO.getIsActive());
        }
        // Timestamps are auto

        SupplierMaterialPrice savedPriceEntry = supplierMaterialPriceRepository.save(newPriceEntry);
        logPriceChange(savedPriceEntry, null, changedByUser, "Initial price setting.");

        return supplierMaterialPriceMapper.toDto(savedPriceEntry);
    }

    @Override
    @Transactional
    public SupplierMaterialPriceResponseDTO updatePrice(Long priceId, SupplierMaterialPriceRequestDTO requestDTO, String changedByUser) {
        SupplierMaterialPrice existingPriceEntry = supplierMaterialPriceRepository.findById(priceId)
                .orElseThrow(() -> new ResourceNotFoundException("SupplierMaterialPrice entry not found with id: " + priceId));

        // Create a snapshot of old values for logging before updating
        SupplierMaterialPrice oldPriceDetailsSnapshot = new SupplierMaterialPrice();
        oldPriceDetailsSnapshot.setPrice(existingPriceEntry.getPrice());
        oldPriceDetailsSnapshot.setEffectiveFromDate(existingPriceEntry.getEffectiveFromDate());
        oldPriceDetailsSnapshot.setEffectiveToDate(existingPriceEntry.getEffectiveToDate());
        // Note: this snapshot doesn't have supplier/material, but logPriceChange uses the updated existingPriceEntry for those.

        checkForOverlappingPrices(requestDTO, priceId); // Exclude current entry from overlap check

        // Update fields from DTO
        // Note: We typically don't change supplierId or masterMaterialId for an *existing* price entry.
        // If that's needed, it's usually deleting the old one and creating a new one.
        // For now, assuming supplier & material remain the same for an update.
        if (!existingPriceEntry.getSupplier().getId().equals(requestDTO.getSupplierId()) ||
                !existingPriceEntry.getMasterMaterial().getId().equals(requestDTO.getMasterMaterialId())) {
            throw new InvalidDataException("Supplier and Master Material cannot be changed for an existing price entry. Create a new one instead.");
        }

        existingPriceEntry.setPrice(requestDTO.getPrice());
        existingPriceEntry.setUnit(requestDTO.getUnit());
        existingPriceEntry.setEffectiveFromDate(requestDTO.getEffectiveFromDate());
        existingPriceEntry.setEffectiveToDate(requestDTO.getEffectiveToDate());
        if (requestDTO.getIsActive() != null) {
            existingPriceEntry.setActive(requestDTO.getIsActive());
        }
        // Timestamps updated automatically

        SupplierMaterialPrice updatedPriceEntry = supplierMaterialPriceRepository.save(existingPriceEntry);
        logPriceChange(updatedPriceEntry, oldPriceDetailsSnapshot, changedByUser, "Price details updated.");

        return supplierMaterialPriceMapper.toDto(updatedPriceEntry);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SupplierMaterialPriceResponseDTO> getPriceById(Long priceId) {
        return supplierMaterialPriceRepository.findById(priceId)
                .map(supplierMaterialPriceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierMaterialPriceResponseDTO> getPricesBySupplierId(Long supplierId) {
        return supplierMaterialPriceMapper.toDtoList(
                supplierMaterialPriceRepository.findBySupplierId(supplierId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierMaterialPriceResponseDTO> getPricesByMasterMaterialId(Long masterMaterialId) {
        return supplierMaterialPriceMapper.toDtoList(
                supplierMaterialPriceRepository.findByMasterMaterialId(masterMaterialId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierMaterialPriceResponseDTO> getPricesBySupplierAndMasterMaterial(Long supplierId, Long masterMaterialId) {
        return supplierMaterialPriceMapper.toDtoList(
                supplierMaterialPriceRepository.findBySupplierIdAndMasterMaterialIdOrderByEffectiveFromDateDesc(supplierId, masterMaterialId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SupplierMaterialPriceResponseDTO> getActivePriceForSupplierMaterialUnit(
            Long supplierId, Long masterMaterialId, String unit, LocalDate date) {
        return supplierMaterialPriceRepository.findActivePrice(supplierId, masterMaterialId, unit, date)
                .map(supplierMaterialPriceMapper::toDto);
    }

    @Override
    @Transactional
    public void deactivatePrice(Long priceId, String changedByUser) {
        SupplierMaterialPrice priceEntry = supplierMaterialPriceRepository.findById(priceId)
                .orElseThrow(() -> new ResourceNotFoundException("SupplierMaterialPrice entry not found with id: " + priceId));

        if (!priceEntry.isActive()) {
            throw new InvalidDataException("Price entry is already inactive.");
        }

        SupplierMaterialPrice oldDetailsSnapshot = new SupplierMaterialPrice(); // For logging
        oldDetailsSnapshot.setPrice(priceEntry.getPrice());
        oldDetailsSnapshot.setEffectiveFromDate(priceEntry.getEffectiveFromDate());
        oldDetailsSnapshot.setEffectiveToDate(priceEntry.getEffectiveToDate());
        // Note: logPriceChange doesn't log old/new isActive status, this could be enhanced.

        priceEntry.setActive(false);
        // Optionally, set effectiveToDate to today if it's null or in the future
        if (priceEntry.getEffectiveToDate() == null || priceEntry.getEffectiveToDate().isAfter(LocalDate.now())) {
            // priceEntry.setEffectiveToDate(LocalDate.now()); // Business rule: deactivating also ends its validity today
        }
        SupplierMaterialPrice deactivatedPrice = supplierMaterialPriceRepository.save(priceEntry);
        logPriceChange(deactivatedPrice, oldDetailsSnapshot, changedByUser, "Price entry deactivated.");
    }

    @Override
    @Transactional(readOnly = true)
    public List<PriceRevisionLogDTO> getPriceRevisionHistory(Long supplierMaterialPriceId) {
        if (!supplierMaterialPriceRepository.existsById(supplierMaterialPriceId)) {
            throw new ResourceNotFoundException("SupplierMaterialPrice entry not found with id: " + supplierMaterialPriceId);
        }
        List<PriceRevisionLog> logs = priceRevisionLogRepository.findBySupplierMaterialPriceIdOrderByChangeTimestampDesc(supplierMaterialPriceId);
        return priceRevisionLogMapper.toDtoList(logs);
    }
}