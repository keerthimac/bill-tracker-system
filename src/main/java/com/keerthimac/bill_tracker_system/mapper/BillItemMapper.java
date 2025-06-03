package com.keerthimac.bill_tracker_system.mapper;

import com.keerthimac.bill_tracker_system.dto.BillItemRequestDTO;
import com.keerthimac.bill_tracker_system.dto.BillItemResponseDTO;
import com.keerthimac.bill_tracker_system.entity.BillItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {ItemCategoryMapper.class}) // 'uses' allows this mapper to use ItemCategoryMapper
public interface BillItemMapper {

    BillItemMapper INSTANCE = Mappers.getMapper(BillItemMapper.class);

    // For mapping from BillItem entity to BillItemResponseDTO
    // itemCategory will be handled by ItemCategoryMapper
    @Mapping(source = "itemCategory", target = "itemCategory")
    BillItemResponseDTO toResponseDto(BillItem billItem);

    // For mapping from BillItemRequestDTO to BillItem entity
    // We need to ignore 'itemCategory' here because BillItemRequestDTO has 'itemCategoryId'
    // The category will be fetched and set in the service layer.
    // Also ignore itemTotalPrice and grnReceivedForItem as they are set programmatically
    @Mapping(target = "id", ignore = true) // Usually ignore ID for creation from DTO
    @Mapping(target = "purchaseBill", ignore = true) // Will be set in the service
    @Mapping(target = "itemCategory", ignore = true) // Will be set in service using itemCategoryId
    @Mapping(target = "itemTotalPrice", ignore = true) // Calculated in service or @PrePersist
    @Mapping(target = "grnReceivedForItem", ignore = true) // Defaulted or set in service
    @Mapping(target = "remarks", ignore = true) // Usually not set on creation
    BillItem toEntity(BillItemRequestDTO billItemRequestDTO);
}
