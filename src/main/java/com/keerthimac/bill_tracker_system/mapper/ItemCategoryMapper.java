package com.keerthimac.bill_tracker_system.mapper;

import com.keerthimac.bill_tracker_system.dto.ItemCategoryDTO;
import com.keerthimac.bill_tracker_system.entity.ItemCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring") // Makes it a Spring component, injectable
public interface ItemCategoryMapper {

    ItemCategoryMapper INSTANCE = Mappers.getMapper(ItemCategoryMapper.class); // For standalone use if not using Spring injection

    ItemCategoryDTO toDto(ItemCategory itemCategory);
    ItemCategory toEntity(ItemCategoryDTO itemCategoryDTO);
}