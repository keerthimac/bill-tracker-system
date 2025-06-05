package com.keerthimac.bill_tracker_system.service;

import com.keerthimac.bill_tracker_system.dto.MasterMaterialRequestDTO;
import com.keerthimac.bill_tracker_system.dto.MasterMaterialResponseDTO;

import java.util.List;
import java.util.Optional;

public interface MasterMaterialService {

    MasterMaterialResponseDTO createMasterMaterial(MasterMaterialRequestDTO requestDTO);

    Optional<MasterMaterialResponseDTO> getMasterMaterialById(Long id);

    Optional<MasterMaterialResponseDTO> getMasterMaterialByCode(String materialCode);

    List<MasterMaterialResponseDTO> getAllMasterMaterials();

    List<MasterMaterialResponseDTO> searchMasterMaterialsByName(String nameFragment);

    List<MasterMaterialResponseDTO> getMasterMaterialsByCategory(Long itemCategoryId);

    MasterMaterialResponseDTO updateMasterMaterial(Long id, MasterMaterialRequestDTO requestDTO);

    void deleteMasterMaterial(Long id);
}