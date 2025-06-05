package com.keerthimac.bill_tracker_system.service.impl;

import com.keerthimac.bill_tracker_system.dto.BillItemRequestDTO;
import com.keerthimac.bill_tracker_system.dto.PurchaseBillRequestDTO;
import com.keerthimac.bill_tracker_system.dto.PurchaseBillResponseDTO;
import com.keerthimac.bill_tracker_system.exception.ResourceNotFoundException;
import com.keerthimac.bill_tracker_system.mapper.BillItemMapper; // Used for mapping to response, not directly for entity creation from DTO here
import com.keerthimac.bill_tracker_system.mapper.PurchaseBillMapper;
import com.keerthimac.bill_tracker_system.entity.*; // Site, Supplier, PurchaseBill, BillItem, OverallGrnStatus, MasterMaterial
import com.keerthimac.bill_tracker_system.repository.*; // All relevant repositories
import com.keerthimac.bill_tracker_system.service.PurchaseBillService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PurchaseBillServiceImpl implements PurchaseBillService {

    private final PurchaseBillRepository purchaseBillRepository;
    private final BillItemRepository billItemRepository; // Still needed for GRN updates on items
    private final SiteRepository siteRepository;
    private final SupplierRepository supplierRepository;
    private final MasterMaterialRepository masterMaterialRepository; // <<< NEW DEPENDENCY
    // ItemCategoryRepository is no longer directly needed here for BillItem creation

    private final PurchaseBillMapper purchaseBillMapper;
    private final BillItemMapper billItemMapper; // Used for mapping existing BillItems to DTOs

    @Autowired
    public PurchaseBillServiceImpl(PurchaseBillRepository purchaseBillRepository,
                                   BillItemRepository billItemRepository,
                                   SiteRepository siteRepository,
                                   SupplierRepository supplierRepository,
                                   MasterMaterialRepository masterMaterialRepository, // <<< INJECT
                                   PurchaseBillMapper purchaseBillMapper,
                                   BillItemMapper billItemMapper) {
        this.purchaseBillRepository = purchaseBillRepository;
        this.billItemRepository = billItemRepository;
        this.siteRepository = siteRepository;
        this.supplierRepository = supplierRepository;
        this.masterMaterialRepository = masterMaterialRepository; // <<< INITIALIZE
        this.purchaseBillMapper = purchaseBillMapper;
        this.billItemMapper = billItemMapper;
    }

    @Override
    @Transactional
    public PurchaseBillResponseDTO createPurchaseBill(PurchaseBillRequestDTO billRequestDTO) {
        PurchaseBill purchaseBill = purchaseBillMapper.toEntity(billRequestDTO); // Maps header fields

        Site site = siteRepository.findById(billRequestDTO.getSiteId())
                .orElseThrow(() -> new ResourceNotFoundException("Site not found with id: " + billRequestDTO.getSiteId()));
        purchaseBill.setSite(site);

        Supplier supplier = supplierRepository.findById(billRequestDTO.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + billRequestDTO.getSupplierId()));
        purchaseBill.setSupplier(supplier);

        purchaseBill.setOverallGrnStatus(OverallGrnStatus.PENDING); // Default status

        List<BillItem> billItemsEntities = new ArrayList<>();
        BigDecimal totalBillAmount = BigDecimal.ZERO;

        if (billRequestDTO.getItems() != null && !billRequestDTO.getItems().isEmpty()) {
            for (BillItemRequestDTO itemDTO : billRequestDTO.getItems()) {
                MasterMaterial masterMaterial = masterMaterialRepository.findById(itemDTO.getMasterMaterialId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "MasterMaterial not found with id: " + itemDTO.getMasterMaterialId() +
                                        " for item with quantity " + itemDTO.getQuantity())); // Add more context to error

                BillItem billItemEntity = new BillItem(); // Manually creating entity
                // Or use billItemMapper.toEntity(itemDTO) if you want to map quantity, unit, unitPrice via mapper
                // but then ensure masterMaterial is set correctly.

                billItemEntity.setPurchaseBill(purchaseBill); // Link to parent
                billItemEntity.setMasterMaterial(masterMaterial);
                billItemEntity.setQuantity(itemDTO.getQuantity());
                billItemEntity.setUnit(itemDTO.getUnit());
                billItemEntity.setUnitPrice(itemDTO.getUnitPrice());
                // itemTotalPrice will be calculated by @PrePersist in BillItem entity
                // grnReceivedForItem defaults to false in BillItem entity

                // Calculate itemTotalPrice explicitly here if not relying solely on @PrePersist for accumulation
                if (itemDTO.getQuantity() != null && itemDTO.getUnitPrice() != null) {
                    BigDecimal itemTotal = itemDTO.getQuantity().multiply(itemDTO.getUnitPrice());
                    totalBillAmount = totalBillAmount.add(itemTotal);
                    billItemEntity.setItemTotalPrice(itemTotal); // Also set it on entity if @PrePersist might not have run yet for accumulation
                } else {
                    billItemEntity.setItemTotalPrice(BigDecimal.ZERO);
                }


                billItemsEntities.add(billItemEntity);
            }
        }
        purchaseBill.setBillItems(billItemsEntities);
        purchaseBill.setTotalAmount(totalBillAmount);

        PurchaseBill savedBill = purchaseBillRepository.save(purchaseBill);
        return purchaseBillMapper.toDto(savedBill);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PurchaseBillResponseDTO> getPurchaseBillById(Long id) {
        return purchaseBillRepository.findById(id)
                .map(purchaseBillMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseBillResponseDTO> getAllPurchaseBills() {
        return purchaseBillMapper.toDtoList(purchaseBillRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseBillResponseDTO> getPurchaseBillsBySite(Long siteId) {
        if (!siteRepository.existsById(siteId)) {
            throw new ResourceNotFoundException("Site not found with id: " + siteId);
        }
        return purchaseBillMapper.toDtoList(purchaseBillRepository.findBySiteId(siteId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseBillResponseDTO> getPurchaseBillsByDateRange(LocalDate startDate, LocalDate endDate) {
        return purchaseBillMapper.toDtoList(purchaseBillRepository.findByBillDateBetween(startDate, endDate));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseBillResponseDTO> getPurchaseBillsBySiteAndDateRange(Long siteId, LocalDate startDate, LocalDate endDate) {
        if (!siteRepository.existsById(siteId)) {
            throw new ResourceNotFoundException("Site not found with id: " + siteId);
        }
        return purchaseBillMapper.toDtoList(purchaseBillRepository.findBySiteIdAndBillDateBetween(siteId, startDate, endDate));
    }

    @Override
    @Transactional
    public PurchaseBillResponseDTO updatePurchaseBillDetails(Long billId, PurchaseBillRequestDTO billRequestDTO) {
        PurchaseBill bill = purchaseBillRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseBill not found with id: " + billId));

        // Update header fields
        bill.setBillNumber(billRequestDTO.getBillNumber());
        bill.setBillDate(billRequestDTO.getBillDate());

        if (billRequestDTO.getSiteId() != null && (bill.getSite() == null || !bill.getSite().getId().equals(billRequestDTO.getSiteId()))) {
            Site site = siteRepository.findById(billRequestDTO.getSiteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Site not found with id: " + billRequestDTO.getSiteId()));
            bill.setSite(site);
        }

        if (billRequestDTO.getSupplierId() != null && (bill.getSupplier() == null || !bill.getSupplier().getId().equals(billRequestDTO.getSupplierId()))) {
            Supplier supplier = supplierRepository.findById(billRequestDTO.getSupplierId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + billRequestDTO.getSupplierId()));
            bill.setSupplier(supplier);
        }

        // TODO: Implement robust item update logic if items are part of this DTO for update
        // This could involve deleting old items, updating existing, adding new ones.
        // For now, if billRequestDTO.getItems() is provided, it's complex to diff and update.
        // A common pattern is to have separate endpoints for managing items of an existing bill.
        // If we are to replace items:
        /*
        if (billRequestDTO.getItems() != null) {
            bill.getBillItems().clear(); // This would require orphanRemoval=true on the @OneToMany in PurchaseBill
                                        // Or manually delete from billItemRepository
            BigDecimal newTotalAmount = BigDecimal.ZERO;
            for (BillItemRequestDTO itemDTO : billRequestDTO.getItems()) {
                MasterMaterial masterMaterial = masterMaterialRepository.findById(itemDTO.getMasterMaterialId())
                        .orElseThrow(() -> new ResourceNotFoundException("MasterMaterial not found for item: " + itemDTO.getMasterMaterialId()));
                BillItem billItemEntity = new BillItem();
                billItemEntity.setPurchaseBill(bill);
                billItemEntity.setMasterMaterial(masterMaterial);
                billItemEntity.setQuantity(itemDTO.getQuantity());
                billItemEntity.setUnit(itemDTO.getUnit());
                billItemEntity.setUnitPrice(itemDTO.getUnitPrice());
                // itemTotalPrice will be calculated by @PrePersist
                if (itemDTO.getQuantity() != null && itemDTO.getUnitPrice() != null) {
                    newTotalAmount = newTotalAmount.add(itemDTO.getQuantity().multiply(itemDTO.getUnitPrice()));
                }
                bill.getBillItems().add(billItemEntity);
            }
            bill.setTotalAmount(newTotalAmount);
        }
        */

        PurchaseBill updatedBill = purchaseBillRepository.save(bill);
        return purchaseBillMapper.toDto(updatedBill);
    }

    @Override
    @Transactional
    public PurchaseBillResponseDTO updateGrnReceivedForItem(Long billItemId, boolean received, String remarks) {
        BillItem billItem = billItemRepository.findById(billItemId)
                .orElseThrow(() -> new ResourceNotFoundException("BillItem not found with id: " + billItemId));

        billItem.setGrnReceivedForItem(received);
        if (remarks != null) {
            billItem.setRemarks(remarks);
        }
        // BillItem is saved implicitly by saving its parent PurchaseBill if cascade type is appropriate,
        // or explicitly save BillItem then its parent (PurchaseBill) to trigger overall status update.
        // The current purchaseBillService.updateOverallBillGrnStatus in backend already saves the purchaseBill.
        // So, directly saving billItem might be redundant if the parent is saved after.
        // For clarity, let's save the billItem and then ensure the parent bill is re-evaluated.
        billItemRepository.save(billItem); // Make sure this save cascades to PurchaseBill's updatedAt for example

        // After updating an item, recalculate and save the overall GRN status for the parent bill
        return updateOverallBillGrnStatus(billItem.getPurchaseBill().getId());
    }

    @Override
    @Transactional
    public PurchaseBillResponseDTO updateOverallBillGrnStatus(Long billId) {
        PurchaseBill bill = purchaseBillRepository.findByIdWithItems(billId) // Assuming a method that fetches items eagerly
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseBill not found with id: " + billId));

        if (bill.getBillItems() == null || bill.getBillItems().isEmpty()) {
            bill.setOverallGrnStatus(OverallGrnStatus.PENDING);
        } else {
            long totalItems = bill.getBillItems().size();
            long receivedItems = bill.getBillItems().stream().filter(BillItem::isGrnReceivedForItem).count();

            if (receivedItems == 0) {
                bill.setOverallGrnStatus(OverallGrnStatus.PENDING);
            } else if (receivedItems < totalItems) {
                bill.setOverallGrnStatus(OverallGrnStatus.PARTIALLY_RECEIVED);
            } else { // receivedItems == totalItems
                bill.setOverallGrnStatus(OverallGrnStatus.FULLY_RECEIVED);
            }
        }
        PurchaseBill updatedBill = purchaseBillRepository.save(bill);
        return purchaseBillMapper.toDto(updatedBill);
    }

    @Override
    @Transactional
    public PurchaseBillResponseDTO updateGrnHardcopyStatus(Long billId, boolean receivedByPurchaser, boolean handedToAccountant) {
        PurchaseBill bill = purchaseBillRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseBill not found with id: " + billId));

        bill.setGrnHardcopyReceivedByPurchaser(receivedByPurchaser);
        bill.setGrnHardcopyHandedToAccountant(handedToAccountant);

        PurchaseBill updatedBill = purchaseBillRepository.save(bill);
        return purchaseBillMapper.toDto(updatedBill);
    }

    @Override
    @Transactional
    public PurchaseBillResponseDTO updateBillImagePath(Long billId, String imagePath) {
        PurchaseBill bill = purchaseBillRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseBill not found with id: " + billId));
        bill.setBillImagePath(imagePath);
        PurchaseBill updatedBill = purchaseBillRepository.save(bill);
        return purchaseBillMapper.toDto(updatedBill);
    }

    @Override
    @Transactional
    public void deletePurchaseBill(Long id) {
        if (!purchaseBillRepository.existsById(id)) {
            throw new ResourceNotFoundException("PurchaseBill not found with id: " + id + ". Cannot delete.");
        }
        purchaseBillRepository.deleteById(id);
    }
}