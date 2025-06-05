package com.keerthimac.bill_tracker_system.mapper;

import com.keerthimac.bill_tracker_system.dto.BillItemRequestDTO;
import com.keerthimac.bill_tracker_system.dto.BillItemResponseDTO;
import com.keerthimac.bill_tracker_system.entity.BillItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring") // ItemCategoryMapper is not directly used here anymore for mapping to BillItemResponseDTO's category
public interface BillItemMapper {

    @Mappings({
            @Mapping(source = "masterMaterial.id", target = "masterMaterialId"),
            @Mapping(source = "masterMaterial.materialCode", target = "masterMaterialCode"),
            @Mapping(source = "masterMaterial.name", target = "masterMaterialName"),
            @Mapping(source = "masterMaterial.itemCategory.name", target = "itemCategoryName")
            // grnReceivedForItem, remarks, quantity, unit, unitPrice, itemTotalPrice should map by name
    })
    BillItemResponseDTO toResponseDto(BillItem billItem);

    List<BillItemResponseDTO> toResponseDtoList(List<BillItem> billItems);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "purchaseBill", ignore = true),    // Set by service
            @Mapping(target = "masterMaterial", ignore = true), // Set by service using masterMaterialId from DTO
            @Mapping(target = "itemTotalPrice", ignore = true),  // Calculated by @PrePersist/@PreUpdate
            @Mapping(target = "grnReceivedForItem", ignore = true), // Defaults in entity or set by service
            @Mapping(target = "remarks", ignore = true)          // Optional, set later
    })
    BillItem toEntity(BillItemRequestDTO billItemRequestDTO);
}