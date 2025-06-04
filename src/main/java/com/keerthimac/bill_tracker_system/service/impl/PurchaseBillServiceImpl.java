package com.keerthimac.bill_tracker_system.service.impl;

import com.keerthimac.bill_tracker_system.dto.BillItemRequestDTO;
import com.keerthimac.bill_tracker_system.dto.PurchaseBillRequestDTO;
import com.keerthimac.bill_tracker_system.dto.PurchaseBillResponseDTO;
import com.keerthimac.bill_tracker_system.entity.*;
import com.keerthimac.bill_tracker_system.exception.ResourceNotFoundException;
import com.keerthimac.bill_tracker_system.mapper.BillItemMapper;
import com.keerthimac.bill_tracker_system.mapper.PurchaseBillMapper;
import com.keerthimac.bill_tracker_system.repository.*;
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
    private final BillItemRepository billItemRepository;
    private final SiteRepository siteRepository;
    private final ItemCategoryRepository itemCategoryRepository;
    private final SupplierRepository supplierRepository;

    private final PurchaseBillMapper purchaseBillMapper;
    private final BillItemMapper billItemMapper;

    @Autowired
    public PurchaseBillServiceImpl(PurchaseBillRepository purchaseBillRepository,
                                   BillItemRepository billItemRepository,
                                   SiteRepository siteRepository,
                                   ItemCategoryRepository itemCategoryRepository,
                                   SupplierRepository supplierRepository,
                                   PurchaseBillMapper purchaseBillMapper,
                                   BillItemMapper billItemMapper) {
        this.purchaseBillRepository = purchaseBillRepository;
        this.billItemRepository = billItemRepository;
        this.siteRepository = siteRepository;
        this.itemCategoryRepository = itemCategoryRepository;
        this.supplierRepository = supplierRepository;
        this.purchaseBillMapper = purchaseBillMapper;
        this.billItemMapper = billItemMapper;
    }

    @Override
    @Transactional
    public PurchaseBillResponseDTO createPurchaseBill(PurchaseBillRequestDTO billRequestDTO) {
        PurchaseBill purchaseBill = purchaseBillMapper.toEntity(billRequestDTO);

        Site site = siteRepository.findById(billRequestDTO.getSiteId())
                .orElseThrow(() -> new ResourceNotFoundException("Site not found with id: " + billRequestDTO.getSiteId()));
        purchaseBill.setSite(site);

        Supplier supplier = supplierRepository.findById(billRequestDTO.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + billRequestDTO.getSupplierId()));
        purchaseBill.setSupplier(supplier);

        purchaseBill.setOverallGrnStatus(OverallGrnStatus.PENDING);

        List<BillItem> billItems = new ArrayList<>();
        BigDecimal totalBillAmount = BigDecimal.ZERO;

        if (billRequestDTO.getItems() != null) {
            for (BillItemRequestDTO itemDTO : billRequestDTO.getItems()) {
                BillItem billItem = billItemMapper.toEntity(itemDTO);

                ItemCategory category = itemCategoryRepository.findById(itemDTO.getItemCategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("ItemCategory not found with id: " + itemDTO.getItemCategoryId()));
                billItem.setItemCategory(category);

                billItem.setQuantity(itemDTO.getQuantity());
                billItem.setUnitPrice(itemDTO.getUnitPrice());

                if (itemDTO.getQuantity() != null && itemDTO.getUnitPrice() != null) {
                    billItem.setItemTotalPrice(itemDTO.getQuantity().multiply(itemDTO.getUnitPrice()));
                    totalBillAmount = totalBillAmount.add(billItem.getItemTotalPrice());
                } else {
                    billItem.setItemTotalPrice(BigDecimal.ZERO);
                }

                billItem.setGrnReceivedForItem(false);
                billItem.setPurchaseBill(purchaseBill);
                billItems.add(billItem);
            }
        }
        purchaseBill.setBillItems(billItems);
        purchaseBill.setTotalAmount(totalBillAmount);

        PurchaseBill savedBill = purchaseBillRepository.save(purchaseBill);
        return purchaseBillMapper.toResponseDto(savedBill);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PurchaseBillResponseDTO> getPurchaseBillById(Long id) {
        return purchaseBillRepository.findById(id)
                .map(purchaseBillMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseBillResponseDTO> getAllPurchaseBills() {
        List<PurchaseBill> bills = purchaseBillRepository.findAll();
        return purchaseBillMapper.toResponseDtoList(bills);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseBillResponseDTO> getPurchaseBillsBySite(Long siteId) {
        if (!siteRepository.existsById(siteId)) {
            throw new ResourceNotFoundException("Site not found with id: " + siteId);
        }
        List<PurchaseBill> bills = purchaseBillRepository.findBySiteId(siteId);
        return purchaseBillMapper.toResponseDtoList(bills);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseBillResponseDTO> getPurchaseBillsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<PurchaseBill> bills = purchaseBillRepository.findByBillDateBetween(startDate, endDate);
        return purchaseBillMapper.toResponseDtoList(bills);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseBillResponseDTO> getPurchaseBillsBySiteAndDateRange(Long siteId, LocalDate startDate, LocalDate endDate) {
        if (!siteRepository.existsById(siteId)) {
            throw new ResourceNotFoundException("Site not found with id: " + siteId + " when searching by site and date range.");
        }
        List<PurchaseBill> bills = purchaseBillRepository.findBySiteIdAndBillDateBetween(siteId, startDate, endDate);
        return purchaseBillMapper.toResponseDtoList(bills);
    }

    @Override
    @Transactional
    public PurchaseBillResponseDTO updatePurchaseBillDetails(Long billId, PurchaseBillRequestDTO billRequestDTO) {
        PurchaseBill bill = purchaseBillRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseBill not found with id: " + billId));

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

        // Note: Comprehensive update of bill items from billRequestDTO.getItems() is not implemented here.
        // This would require logic to add, remove, or update individual items and recalculate totalAmount.
        // For simplicity, this method currently focuses on non-item details of the PurchaseBill.

        PurchaseBill updatedBill = purchaseBillRepository.save(bill);
        return purchaseBillMapper.toResponseDto(updatedBill);
    }

    @Override
    @Transactional
    public PurchaseBillResponseDTO updateGrnReceivedForItem(Long billItemId, boolean received, String remarks) {
        BillItem billItem = billItemRepository.findById(billItemId)
                .orElseThrow(() -> new ResourceNotFoundException("BillItem not found with id: " + billItemId));

        billItem.setGrnReceivedForItem(received);
        if (remarks != null) { // Only update remarks if provided
            billItem.setRemarks(remarks);
        }
        billItemRepository.save(billItem);

        // After updating an item, recalculate and save the overall GRN status for the parent bill
        return updateOverallBillGrnStatus(billItem.getPurchaseBill().getId());
    }

    @Override
    @Transactional
    public PurchaseBillResponseDTO updateOverallBillGrnStatus(Long billId) {
        PurchaseBill bill = purchaseBillRepository.findById(billId)
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
        return purchaseBillMapper.toResponseDto(updatedBill);
    }

    @Override
    @Transactional
    public PurchaseBillResponseDTO updateGrnHardcopyStatus(Long billId, boolean receivedByPurchaser, boolean handedToAccountant) {
        PurchaseBill bill = purchaseBillRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseBill not found with id: " + billId));

        bill.setGrnHardcopyReceivedByPurchaser(receivedByPurchaser);
        bill.setGrnHardcopyHandedToAccountant(handedToAccountant);

        PurchaseBill updatedBill = purchaseBillRepository.save(bill);
        return purchaseBillMapper.toResponseDto(updatedBill);
    }

    @Override
    @Transactional
    public PurchaseBillResponseDTO updateBillImagePath(Long billId, String imagePath) {
        PurchaseBill bill = purchaseBillRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseBill not found with id: " + billId));
        bill.setBillImagePath(imagePath);
        PurchaseBill updatedBill = purchaseBillRepository.save(bill);
        return purchaseBillMapper.toResponseDto(updatedBill);
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