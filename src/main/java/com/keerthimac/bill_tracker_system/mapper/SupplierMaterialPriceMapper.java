package com.keerthimac.bill_tracker_system.mapper; // Your mapper package

import com.keerthimac.bill_tracker_system.dto.SupplierMaterialPriceRequestDTO;
import com.keerthimac.bill_tracker_system.dto.SupplierMaterialPriceResponseDTO;
import com.keerthimac.bill_tracker_system.entity.SupplierMaterialPrice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring", uses = {SupplierMapper.class, MasterMaterialMapper.class})
public interface SupplierMaterialPriceMapper {

    /**
     * Converts a SupplierMaterialPrice entity to a SupplierMaterialPriceResponseDTO.
     * Nested 'supplier' and 'masterMaterial' will be mapped using their respective mappers.
     * @param supplierMaterialPrice The SupplierMaterialPrice entity.
     * @return The corresponding SupplierMaterialPriceResponseDTO.
     */
    // No explicit @Mapping needed for supplier and masterMaterial if field names in DTO
    // match entity fields and the respective mappers are in 'uses'.
    SupplierMaterialPriceResponseDTO toDto(SupplierMaterialPrice supplierMaterialPrice);

    /**
     * Converts a list of SupplierMaterialPrice entities to a list of SupplierMaterialPriceResponseDTOs.
     * @param prices The list of SupplierMaterialPrice entities.
     * @return The list of corresponding SupplierMaterialPriceResponseDTOs.
     */
    List<SupplierMaterialPriceResponseDTO> toDtoList(List<SupplierMaterialPrice> prices);

    /**
     * Converts a SupplierMaterialPriceRequestDTO to a SupplierMaterialPrice entity.
     * The 'supplier' and 'masterMaterial' entity objects themselves will be set in the service layer
     * using 'supplierId' and 'masterMaterialId' from the DTO.
     * 'id', 'createdAt', and 'updatedAt' are ignored as they are auto-generated or managed by JPA.
     * The 'isActive' field from the DTO (Boolean) will map to the entity's 'isActive' (boolean).
     * If DTO's isActive is null, entity's isActive will become false. Service layer should handle
     * defaulting isActive to true for new entries if DTO.isActive is null and that's the desired behavior.
     * @param requestDTO The SupplierMaterialPriceRequestDTO.
     * @return The corresponding SupplierMaterialPrice entity.
     */
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "supplier", ignore = true),         // Service will set this using supplierId
            @Mapping(target = "masterMaterial", ignore = true),  // Service will set this using masterMaterialId
            @Mapping(target = "createdAt", ignore = true),       // Auto-generated by @CreationTimestamp
            @Mapping(target = "updatedAt", ignore = true)        // Auto-generated by @UpdateTimestamp
            // isActive will be mapped by name. If requestDTO.getIsActive() is null, entity.isActive will be false.
            // The entity has a default `isActive = true`, but a setter call with `false` will override it.
    })
    SupplierMaterialPrice toEntity(SupplierMaterialPriceRequestDTO requestDTO);
}