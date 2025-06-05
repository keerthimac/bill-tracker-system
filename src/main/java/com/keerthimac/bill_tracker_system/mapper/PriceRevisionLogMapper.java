package com.keerthimac.bill_tracker_system.mapper;

import com.keerthimac.bill_tracker_system.dto.PriceRevisionLogDTO;
import com.keerthimac.bill_tracker_system.entity.PriceRevisionLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PriceRevisionLogMapper {
    @Mapping(source = "supplierMaterialPrice.supplier.name", target = "supplierName")
    @Mapping(source = "supplierMaterialPrice.masterMaterial.name", target = "masterMaterialName")
    PriceRevisionLogDTO toDto(PriceRevisionLog log);
    List<PriceRevisionLogDTO> toDtoList(List<PriceRevisionLog> logs);
}