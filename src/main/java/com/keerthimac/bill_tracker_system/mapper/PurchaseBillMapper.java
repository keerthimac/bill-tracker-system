package com.keerthimac.bill_tracker_system.mapper;

import com.keerthimac.bill_tracker_system.dto.PurchaseBillRequestDTO;
import com.keerthimac.bill_tracker_system.dto.PurchaseBillResponseDTO;
import com.keerthimac.bill_tracker_system.entity.OverallGrnStatus; // Ensure this enum is in your model package
import com.keerthimac.bill_tracker_system.entity.PurchaseBill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = {
        SiteMapper.class,
        SupplierMapper.class,
        BillItemMapper.class // This mapper now handles BillItem -> BillItemResponseDTO with MasterMaterial details
})
public interface PurchaseBillMapper {

    /**
     * Converts a PurchaseBill entity to a PurchaseBillResponseDTO.
     * Uses SiteMapper, SupplierMapper, and BillItemMapper for nested objects/lists.
     * @param purchaseBill The PurchaseBill entity.
     * @return The corresponding PurchaseBillResponseDTO.
     */
    @Mappings({
            @Mapping(source = "supplier", target = "supplier"), // Uses SupplierMapper
            @Mapping(source = "site", target = "site"),         // Uses SiteMapper
            @Mapping(source = "billItems", target = "billItems"), // Uses BillItemMapper for list elements
            @Mapping(source = "overallGrnStatus", target = "overallGrnStatus", qualifiedByName = "enumToString")
            // Fields like id, billNumber, billDate, billImagePath, grn statuses, totalAmount, createdAt, updatedAt
            // should map by name if types are compatible.
    })
    PurchaseBillResponseDTO toDto(PurchaseBill purchaseBill);

    /**
     * Converts a list of PurchaseBill entities to a list of PurchaseBillResponseDTOs.
     * @param purchaseBills List of PurchaseBill entities.
     * @return List of corresponding PurchaseBillResponseDTOs.
     */
    List<PurchaseBillResponseDTO> toDtoList(List<PurchaseBill> purchaseBills);

    /**
     * Converts a PurchaseBillRequestDTO to a PurchaseBill entity.
     * Ignores fields that are auto-generated or set by the service layer.
     * @param requestDTO The PurchaseBillRequestDTO.
     * @return The corresponding PurchaseBill entity.
     */
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "supplier", ignore = true),     // Service sets from supplierId
            @Mapping(target = "site", ignore = true),         // Service sets from siteId
            @Mapping(target = "billItems", ignore = true),    // Service creates and sets these from DTO items
            @Mapping(target = "overallGrnStatus", ignore = true), // Set by service/default
            @Mapping(target = "totalAmount", ignore = true),      // Calculated by service
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true)
            // billImagePath might be set separately after file upload
    })
    PurchaseBill toEntity(PurchaseBillRequestDTO requestDTO);

    /**
     * Helper method to convert OverallGrnStatus enum to String for DTO.
     * @param status The OverallGrnStatus enum.
     * @return String representation of the status, or null.
     */
    @Named("enumToString")
    default String enumToString(OverallGrnStatus status) {
        return status != null ? status.name() : null;
    }
}