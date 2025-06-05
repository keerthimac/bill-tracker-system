package com.keerthimac.bill_tracker_system.service;

import com.keerthimac.bill_tracker_system.dto.PriceRevisionLogDTO; // Assuming a DTO for logs if needed for response
import com.keerthimac.bill_tracker_system.dto.SupplierMaterialPriceRequestDTO;
import com.keerthimac.bill_tracker_system.dto.SupplierMaterialPriceResponseDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SupplierMaterialPriceService {

    SupplierMaterialPriceResponseDTO addPrice(SupplierMaterialPriceRequestDTO requestDTO, String changedByUser);

    SupplierMaterialPriceResponseDTO updatePrice(Long priceId, SupplierMaterialPriceRequestDTO requestDTO, String changedByUser);

    Optional<SupplierMaterialPriceResponseDTO> getPriceById(Long priceId);

    List<SupplierMaterialPriceResponseDTO> getPricesBySupplierId(Long supplierId);

    List<SupplierMaterialPriceResponseDTO> getPricesByMasterMaterialId(Long masterMaterialId);

    List<SupplierMaterialPriceResponseDTO> getPricesBySupplierAndMasterMaterial(Long supplierId, Long masterMaterialId);

    Optional<SupplierMaterialPriceResponseDTO> getActivePriceForSupplierMaterialUnit(
            Long supplierId, Long masterMaterialId, String unit, LocalDate date);

    void deactivatePrice(Long priceId, String changedByUser); // Instead of hard delete

    List<PriceRevisionLogDTO> getPriceRevisionHistory(Long supplierMaterialPriceId); // For viewing log
}