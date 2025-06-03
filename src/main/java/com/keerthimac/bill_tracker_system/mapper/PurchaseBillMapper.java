package com.keerthimac.bill_tracker_system.mapper;

import com.keerthimac.bill_tracker_system.dto.PurchaseBillRequestDTO;
import com.keerthimac.bill_tracker_system.dto.PurchaseBillResponseDTO;
import com.keerthimac.bill_tracker_system.entity.OverallGrnStatus;
import com.keerthimac.bill_tracker_system.entity.PurchaseBill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
// No need to import Mappers here if INSTANCE is not used directly in this class

import java.util.List;

// Add SupplierMapper to the 'uses' attribute
@Mapper(componentModel = "spring", uses = {SiteMapper.class, BillItemMapper.class, SupplierMapper.class})
public interface PurchaseBillMapper {

    // PurchaseBillMapper INSTANCE = Mappers.getMapper(PurchaseBillMapper.class); // Can be removed if not used directly

    // From PurchaseBill entity to PurchaseBillResponseDTO
    @Mapping(source = "site", target = "site")             // Uses SiteMapper
    @Mapping(source = "supplier", target = "supplier")     // <<< ADD THIS: Uses SupplierMapper
    @Mapping(source = "billItems", target = "billItems")   // Uses BillItemMapper
    @Mapping(source = "overallGrnStatus", target = "overallGrnStatus", qualifiedByName = "enumToString")
    PurchaseBillResponseDTO toResponseDto(PurchaseBill purchaseBill);

    List<PurchaseBillResponseDTO> toResponseDtoList(List<PurchaseBill> purchaseBills);

    // From PurchaseBillRequestDTO to PurchaseBill entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "site", ignore = true)         // Site will be fetched and set in the service from siteId
    @Mapping(target = "supplier", ignore = true)     // <<< ADD THIS: Supplier will be fetched and set in the service from supplierId
    @Mapping(target = "billItems", ignore = true)    // BillItems will be mapped and set in the service
    @Mapping(target = "billImagePath", ignore = true)
    @Mapping(target = "overallGrnStatus", ignore = true)
    @Mapping(target = "grnHardcopyReceivedByPurchaser", ignore = true)
    @Mapping(target = "grnHardcopyHandedToAccountant", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PurchaseBill toEntity(PurchaseBillRequestDTO purchaseBillRequestDTO);

    @Named("enumToString")
    default String enumToString(OverallGrnStatus status) {
        return status != null ? status.name() : null;
    }
}