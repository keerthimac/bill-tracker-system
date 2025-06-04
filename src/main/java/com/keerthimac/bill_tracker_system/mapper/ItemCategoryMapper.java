package com.keerthimac.bill_tracker_system.mapper; // Your package

import com.keerthimac.bill_tracker_system.dto.ItemCategoryDTO;
import com.keerthimac.bill_tracker_system.entity.ItemCategory;
import org.mapstruct.Mapper;
// import org.mapstruct.factory.Mappers; // Only needed if using INSTANCE directly

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemCategoryMapper {

    // ItemCategoryMapper INSTANCE = Mappers.getMapper(ItemCategoryMapper.class); // Usually not needed if using Spring injection

    ItemCategoryDTO toDto(ItemCategory itemCategory);

    ItemCategory toEntity(ItemCategoryDTO itemCategoryDTO);

    List<ItemCategoryDTO> toDtoList(List<ItemCategory> categories); // Ensure this line is present and correct
}