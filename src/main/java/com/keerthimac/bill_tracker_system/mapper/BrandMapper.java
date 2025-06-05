package com.keerthimac.bill_tracker_system.mapper; // Your mapper package

import com.keerthimac.bill_tracker_system.dto.BrandRequestDTO;
import com.keerthimac.bill_tracker_system.dto.BrandResponseDTO;
import com.keerthimac.bill_tracker_system.entity.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BrandMapper {

    /**
     * Converts a Brand entity to a BrandResponseDTO.
     * Includes mapping for brandImagePath.
     * @param brand The Brand entity.
     * @return The corresponding BrandResponseDTO.
     */
    BrandResponseDTO toDto(Brand brand);

    /**
     * Converts a list of Brand entities to a list of BrandResponseDTOs.
     * @param brands The list of Brand entities.
     * @return The list of corresponding BrandResponseDTOs.
     */
    List<BrandResponseDTO> toDtoList(List<Brand> brands);

    /**
     * Converts a BrandRequestDTO to a Brand entity.
     * 'id', 'createdAt', and 'updatedAt' are ignored as they are auto-generated or managed by JPA.
     * brandImagePath from DTO will be mapped to the entity. How this path is initially populated in DTO
     * (e.g. after an upload) will be handled by service/controller.
     * @param requestDTO The BrandRequestDTO.
     * @return The corresponding Brand entity.
     */
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true)
            // brandImagePath will map by name from DTO to entity
    })
    Brand toEntity(BrandRequestDTO requestDTO);
}