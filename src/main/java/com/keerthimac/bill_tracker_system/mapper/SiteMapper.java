package com.keerthimac.bill_tracker_system.mapper;

import com.keerthimac.bill_tracker_system.dto.SiteDTO;
import com.keerthimac.bill_tracker_system.entity.Site;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers; // Optional if only using Spring injection

import java.util.List;

@Mapper(componentModel = "spring") // Makes this mapper a Spring component
public interface SiteMapper {

    // Optional: if you need to access the mapper instance directly without Spring injection
    // SiteMapper INSTANCE = Mappers.getMapper(SiteMapper.class);

    /**
     * Converts a Site entity to a SiteDTO.
     * @param site The Site entity.
     * @return The corresponding SiteDTO.
     */
    SiteDTO toDto(Site site);

    /**
     * Converts a SiteDTO to a Site entity.
     * Typically used when creating a new Site, so the ID from DTO might be ignored.
     * @param siteDTO The SiteDTO.
     * @return The corresponding Site entity.
     */
    @Mapping(target = "id", ignore = true) // Usually ignore ID when mapping DTO to entity for creation
    Site toEntity(SiteDTO siteDTO);

    /**
     * Converts a list of Site entities to a list of SiteDTOs.
     * @param sites The list of Site entities.
     * @return The list of corresponding SiteDTOs.
     */
    List<SiteDTO> toDtoList(List<Site> sites);
}
