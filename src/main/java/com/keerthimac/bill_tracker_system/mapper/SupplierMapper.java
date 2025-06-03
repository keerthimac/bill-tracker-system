package com.keerthimac.bill_tracker_system.mapper;

import com.keerthimac.bill_tracker_system.dto.SupplierRequestDTO;
import com.keerthimac.bill_tracker_system.dto.SupplierResponseDTO;
import com.keerthimac.bill_tracker_system.entity.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring") // Generates a Spring Bean
public interface SupplierMapper {

    SupplierMapper INSTANCE = Mappers.getMapper(SupplierMapper.class);

    // To DTO
    SupplierResponseDTO toDto(Supplier supplier);

    List<SupplierResponseDTO> toDtoList(List<Supplier> suppliers);

    // To Entity from RequestDTO
    @Mapping(target = "id", ignore = true) // ID is auto-generated or set elsewhere
    Supplier toEntity(SupplierRequestDTO supplierRequestDTO);
}
