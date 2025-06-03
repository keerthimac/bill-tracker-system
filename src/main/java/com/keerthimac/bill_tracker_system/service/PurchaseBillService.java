package com.keerthimac.bill_tracker_system.service;

import com.keerthimac.bill_tracker_system.dto.PurchaseBillRequestDTO;
import com.keerthimac.bill_tracker_system.dto.PurchaseBillResponseDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PurchaseBillService {
    PurchaseBillResponseDTO createPurchaseBill(PurchaseBillRequestDTO billRequestDTO);
    Optional<PurchaseBillResponseDTO> getPurchaseBillById(Long id);
    List<PurchaseBillResponseDTO> getAllPurchaseBills();
    // Consider adding pagination for getAllPurchaseBills later: Page<PurchaseBillResponseDTO> getAllPurchaseBills(Pageable pageable);

    List<PurchaseBillResponseDTO> getPurchaseBillsBySite(Long siteId);
    List<PurchaseBillResponseDTO> getPurchaseBillsByDateRange(LocalDate startDate, LocalDate endDate);
    List<PurchaseBillResponseDTO> getPurchaseBillsBySiteAndDateRange(Long siteId, LocalDate startDate, LocalDate endDate);

    PurchaseBillResponseDTO updatePurchaseBillDetails(Long billId, PurchaseBillRequestDTO billRequestDTO); // For general details, not status updates

    // GRN related updates
    PurchaseBillResponseDTO updateGrnReceivedForItem(Long billItemId, boolean received, String remarks);
    PurchaseBillResponseDTO updateOverallBillGrnStatus(Long billId); // Could be called internally or explicitly
    PurchaseBillResponseDTO updateGrnHardcopyStatus(Long billId, boolean receivedByPurchaser, boolean handedToAccountant);

    // For file upload - this will be a bit more involved
    PurchaseBillResponseDTO updateBillImagePath(Long billId, String imagePath);

    void deletePurchaseBill(Long id);
}
