package com.keerthimac.bill_tracker_system.service.impl;

import com.keerthimac.bill_tracker_system.dto.BillItemRequestDTO;
import com.keerthimac.bill_tracker_system.dto.PurchaseBillRequestDTO;
import com.keerthimac.bill_tracker_system.dto.PurchaseBillResponseDTO;
import com.keerthimac.bill_tracker_system.entity.*;
import com.keerthimac.bill_tracker_system.mapper.BillItemMapper;
import com.keerthimac.bill_tracker_system.mapper.PurchaseBillMapper;
import com.keerthimac.bill_tracker_system.repository.ItemCategoryRepository;
import com.keerthimac.bill_tracker_system.repository.PurchaseBillRepository;
import com.keerthimac.bill_tracker_system.repository.SiteRepository;
import com.keerthimac.bill_tracker_system.repository.BillItemRepository;
import com.keerthimac.bill_tracker_system.service.PurchaseBillService;
import jakarta.persistence.EntityNotFoundException;
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

    // Inject mappers
    private final PurchaseBillMapper purchaseBillMapper;
    private final BillItemMapper billItemMapper;
    // You would also inject SiteMapper and ItemCategoryMapper if used directly here,
    // but they are often used via other mappers (e.g., PurchaseBillMapper uses SiteMapper)

    @Autowired
    public PurchaseBillServiceImpl(PurchaseBillRepository purchaseBillRepository,
                                   BillItemRepository billItemRepository,
                                   SiteRepository siteRepository,
                                   ItemCategoryRepository itemCategoryRepository,
                                   PurchaseBillMapper purchaseBillMapper, // Add mappers to constructor
                                   BillItemMapper billItemMapper) {
        this.purchaseBillRepository = purchaseBillRepository;
        this.billItemRepository = billItemRepository;
        this.siteRepository = siteRepository;
        this.itemCategoryRepository = itemCategoryRepository;
        this.purchaseBillMapper = purchaseBillMapper; // Initialize mappers
        this.billItemMapper = billItemMapper;
    }

    @Override
    @Transactional
    public PurchaseBillResponseDTO createPurchaseBill(PurchaseBillRequestDTO billRequestDTO) {
        // Map DTO to Entity using PurchaseBillMapper
        PurchaseBill purchaseBill = purchaseBillMapper.toEntity(billRequestDTO);

        Site site = siteRepository.findById(billRequestDTO.getSiteId())
                .orElseThrow(() -> new EntityNotFoundException("Site not found with id: " + billRequestDTO.getSiteId()));
        purchaseBill.setSite(site);
        purchaseBill.setOverallGrnStatus(OverallGrnStatus.PENDING);

        List<BillItem> billItems = new ArrayList<>();
        BigDecimal totalBillAmount = BigDecimal.ZERO;

        if (billRequestDTO.getItems() != null) {
            for (BillItemRequestDTO itemDTO : billRequestDTO.getItems()) {
                // Map BillItemRequestDTO to BillItem entity using BillItemMapper
                BillItem billItem = billItemMapper.toEntity(itemDTO);

                ItemCategory category = itemCategoryRepository.findById(itemDTO.getItemCategoryId())
                        .orElseThrow(() -> new EntityNotFoundException("ItemCategory not found with id: " + itemDTO.getItemCategoryId()));
                billItem.setItemCategory(category);

                // Set quantity and unitPrice if not directly mapped or if needing re-assertion
                billItem.setQuantity(itemDTO.getQuantity());
                billItem.setUnitPrice(itemDTO.getUnitPrice());

                if (billItem.getQuantity() != null && billItem.getUnitPrice() != null) {
                    billItem.setItemTotalPrice(billItem.getQuantity().multiply(billItem.getUnitPrice()));
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
        return purchaseBillMapper.toResponseDto(savedBill); // Use mapper
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PurchaseBillResponseDTO> getPurchaseBillById(Long id) {
        return purchaseBillRepository.findById(id)
                .map(purchaseBillMapper::toResponseDto); // Use mapper method reference
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseBillResponseDTO> getAllPurchaseBills() {
        return purchaseBillMapper.toResponseDtoList(purchaseBillRepository.findAll());
    }

    // ... other service methods would also use the mappers ...

    @Override
    @Transactional
    public PurchaseBillResponseDTO updateGrnReceivedForItem(Long billItemId, boolean received, String remarks) {
        BillItem billItem = billItemRepository.findById(billItemId)
                .orElseThrow(() -> new EntityNotFoundException("BillItem not found with id: " + billItemId));

        billItem.setGrnReceivedForItem(received);
        if (remarks != null) {
            billItem.setRemarks(remarks);
        }
        billItemRepository.save(billItem);

        return updateOverallBillGrnStatus(billItem.getPurchaseBill().getId());
    }

    @Override
    @Transactional
    public PurchaseBillResponseDTO updateOverallBillGrnStatus(Long billId) {
        PurchaseBill bill = purchaseBillRepository.findById(billId)
                .orElseThrow(() -> new EntityNotFoundException("PurchaseBill not found with id: " + billId));

        if (bill.getBillItems() == null || bill.getBillItems().isEmpty()) {
            bill.setOverallGrnStatus(OverallGrnStatus.PENDING);
        } else {
            long totalItems = bill.getBillItems().size();
            long receivedItems = bill.getBillItems().stream().filter(BillItem::isGrnReceivedForItem).count();

            if (receivedItems == 0) {
                bill.setOverallGrnStatus(OverallGrnStatus.PENDING);
            } else if (receivedItems < totalItems) {
                bill.setOverallGrnStatus(OverallGrnStatus.PARTIALLY_RECEIVED);
            } else {
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
                .orElseThrow(() -> new EntityNotFoundException("PurchaseBill not found with id: " + billId));

        bill.setGrnHardcopyReceivedByPurchaser(receivedByPurchaser);
        bill.setGrnHardcopyHandedToAccountant(handedToAccountant);

        PurchaseBill updatedBill = purchaseBillRepository.save(bill);
        return purchaseBillMapper.toResponseDto(updatedBill);
    }

    @Override
    @Transactional
    public PurchaseBillResponseDTO updateBillImagePath(Long billId, String imagePath) {
        PurchaseBill bill = purchaseBillRepository.findById(billId)
                .orElseThrow(() -> new EntityNotFoundException("PurchaseBill not found with id: " + billId));
        bill.setBillImagePath(imagePath);
        PurchaseBill updatedBill = purchaseBillRepository.save(bill);
        return purchaseBillMapper.toResponseDto(updatedBill);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseBillResponseDTO> getPurchaseBillsBySite(Long siteId) {
        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new EntityNotFoundException("Site not found: " + siteId));
        List<PurchaseBill> bills = purchaseBillRepository.findBySite(site);
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
        List<PurchaseBill> bills = purchaseBillRepository.findBySiteIdAndBillDateBetween(siteId, startDate, endDate);
        return purchaseBillMapper.toResponseDtoList(bills);
    }

    @Override
    @Transactional
    public PurchaseBillResponseDTO updatePurchaseBillDetails(Long billId, PurchaseBillRequestDTO billRequestDTO) {
        PurchaseBill bill = purchaseBillRepository.findById(billId)
                .orElseThrow(() -> new EntityNotFoundException("PurchaseBill not found with id: " + billId));

        // Update fields from DTO. MapStruct can help here if you define an update method in the mapper
        // e.g., @MappingTarget Bill updateFromDto(PurchaseBillRequestDTO dto, @MappingTarget Bill bill);
        bill.setBillNumber(billRequestDTO.getBillNumber());
        bill.setBillDate(billRequestDTO.getBillDate());
        bill.setSupplierName(billRequestDTO.getSupplierName());

        if (billRequestDTO.getSiteId() != null && (bill.getSite() == null || !bill.getSite().getId().equals(billRequestDTO.getSiteId()))) {
            Site site = siteRepository.findById(billRequestDTO.getSiteId())
                    .orElseThrow(() -> new EntityNotFoundException("Site not found with id: " + billRequestDTO.getSiteId()));
            bill.setSite(site);
        }

        // Item updates for an existing bill is complex (add new, remove old, update existing)
        // This part might need more detailed logic or a different approach,
        // such as dedicated endpoints for adding/removing/updating items on a bill.
        // For now, this example focuses on non-item details.

        PurchaseBill updatedBill = purchaseBillRepository.save(bill);
        return purchaseBillMapper.toResponseDto(updatedBill);
    }

    @Override
    @Transactional
    public void deletePurchaseBill(Long id) {
        if (!purchaseBillRepository.existsById(id)) {
            throw new EntityNotFoundException("PurchaseBill not found with id: " + id);
        }
        purchaseBillRepository.deleteById(id);
    }
}
