package com.keerthimac.bill_tracker_system.service.impl;

import com.keerthimac.bill_tracker_system.dto.MasterMaterialRequestDTO;
import com.keerthimac.bill_tracker_system.dto.MasterMaterialResponseDTO;
import com.keerthimac.bill_tracker_system.exception.DuplicateResourceException;
import com.keerthimac.bill_tracker_system.exception.ResourceNotFoundException;
import com.keerthimac.bill_tracker_system.mapper.MasterMaterialMapper;
import com.keerthimac.bill_tracker_system.entity.ItemCategory;
import com.keerthimac.bill_tracker_system.entity.MasterMaterial;
import com.keerthimac.bill_tracker_system.repository.ItemCategoryRepository;
import com.keerthimac.bill_tracker_system.repository.MasterMaterialRepository;
// TODO: Import BillItemRepository and SupplierMaterialPriceRepository when available for delete check
import com.keerthimac.bill_tracker_system.service.MasterMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils; // For checking if string has text

import java.util.List;
import java.util.Optional;

@Service
public class MasterMaterialServiceImpl implements MasterMaterialService {

    private final MasterMaterialRepository masterMaterialRepository;
    private final ItemCategoryRepository itemCategoryRepository;
    private final MasterMaterialMapper masterMaterialMapper;
    // TODO: Inject BillItemRepository and SupplierMaterialPriceRepository for delete checks

    @Autowired
    public MasterMaterialServiceImpl(MasterMaterialRepository masterMaterialRepository,
                                     ItemCategoryRepository itemCategoryRepository,
                                     MasterMaterialMapper masterMaterialMapper) {
        this.masterMaterialRepository = masterMaterialRepository;
        this.itemCategoryRepository = itemCategoryRepository;
        this.masterMaterialMapper = masterMaterialMapper;
    }

    @Override
    @Transactional
    public MasterMaterialResponseDTO createMasterMaterial(MasterMaterialRequestDTO requestDTO) {
        // Check for duplicate name
        if (masterMaterialRepository.existsByNameIgnoreCase(requestDTO.getName())) {
            throw new DuplicateResourceException("MasterMaterial with name '" + requestDTO.getName() + "' already exists.");
        }
        // Check for duplicate material code if provided
        if (StringUtils.hasText(requestDTO.getMaterialCode()) && masterMaterialRepository.existsByMaterialCodeIgnoreCase(requestDTO.getMaterialCode())) {
            throw new DuplicateResourceException("MasterMaterial with code '" + requestDTO.getMaterialCode() + "' already exists.");
        }

        ItemCategory itemCategory = itemCategoryRepository.findById(requestDTO.getItemCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("ItemCategory not found with id: " + requestDTO.getItemCategoryId()));

        MasterMaterial masterMaterial = masterMaterialMapper.toEntity(requestDTO);
        masterMaterial.setItemCategory(itemCategory);
        // Timestamps (createdAt, updatedAt) will be handled by @CreationTimestamp / @UpdateTimestamp

        MasterMaterial savedMaterial = masterMaterialRepository.save(masterMaterial);
        return masterMaterialMapper.toDto(savedMaterial);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MasterMaterialResponseDTO> getMasterMaterialById(Long id) {
        return masterMaterialRepository.findById(id)
                .map(masterMaterialMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MasterMaterialResponseDTO> getMasterMaterialByCode(String materialCode) {
        return masterMaterialRepository.findByMaterialCodeIgnoreCase(materialCode)
                .map(masterMaterialMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MasterMaterialResponseDTO> getAllMasterMaterials() {
        return masterMaterialMapper.toDtoList(masterMaterialRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MasterMaterialResponseDTO> searchMasterMaterialsByName(String nameFragment) {
        return masterMaterialMapper.toDtoList(masterMaterialRepository.findByNameContainingIgnoreCase(nameFragment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MasterMaterialResponseDTO> getMasterMaterialsByCategory(Long itemCategoryId) {
        if (!itemCategoryRepository.existsById(itemCategoryId)) {
            throw new ResourceNotFoundException("ItemCategory not found with id: " + itemCategoryId + ", cannot fetch materials.");
        }
        return masterMaterialMapper.toDtoList(masterMaterialRepository.findByItemCategoryId(itemCategoryId));
    }

    @Override
    @Transactional
    public MasterMaterialResponseDTO updateMasterMaterial(Long id, MasterMaterialRequestDTO requestDTO) {
        MasterMaterial existingMaterial = masterMaterialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MasterMaterial not found with id: " + id));

        // Check for duplicate name if name is being changed
        if (!existingMaterial.getName().equalsIgnoreCase(requestDTO.getName()) &&
                masterMaterialRepository.existsByNameIgnoreCase(requestDTO.getName())) {
            throw new DuplicateResourceException("Another MasterMaterial with name '" + requestDTO.getName() + "' already exists.");
        }

        // Check for duplicate material code if code is being changed or set
        if (StringUtils.hasText(requestDTO.getMaterialCode())) {
            if ((existingMaterial.getMaterialCode() == null ||
                    !existingMaterial.getMaterialCode().equalsIgnoreCase(requestDTO.getMaterialCode())) &&
                    masterMaterialRepository.existsByMaterialCodeIgnoreCase(requestDTO.getMaterialCode())) {
                throw new DuplicateResourceException("Another MasterMaterial with code '" + requestDTO.getMaterialCode() + "' already exists.");
            }
            existingMaterial.setMaterialCode(requestDTO.getMaterialCode());
        } else {
            existingMaterial.setMaterialCode(null); // Allow clearing the code
        }


        // Update basic fields
        existingMaterial.setName(requestDTO.getName());
        existingMaterial.setDescription(requestDTO.getDescription());
        existingMaterial.setDefaultUnit(requestDTO.getDefaultUnit());

        // Update ItemCategory if changed
        if (!existingMaterial.getItemCategory().getId().equals(requestDTO.getItemCategoryId())) {
            ItemCategory newItemCategory = itemCategoryRepository.findById(requestDTO.getItemCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("ItemCategory not found with id: " + requestDTO.getItemCategoryId()));
            existingMaterial.setItemCategory(newItemCategory);
        }
        // updatedAt will be automatically handled by @UpdateTimestamp

        MasterMaterial updatedMaterial = masterMaterialRepository.save(existingMaterial);
        return masterMaterialMapper.toDto(updatedMaterial);
    }

    @Override
    @Transactional
    public void deleteMasterMaterial(Long id) {
        if (!masterMaterialRepository.existsById(id)) {
            throw new ResourceNotFoundException("MasterMaterial not found with id: " + id + ". Cannot delete.");
        }

        // TODO: IMPORTANT - Check if this MasterMaterial is used in BillItems or SupplierMaterialPrices
        // before allowing deletion. This requires BillItemRepository and SupplierMaterialPriceRepository.
        // Example (conceptual - you'll need the actual repository methods):
        // if (billItemRepository.existsByMasterMaterialId(id) ||
        //     supplierMaterialPriceRepository.existsByMasterMaterialId(id)) {
        //     throw new DataIntegrityViolationException("Cannot delete MasterMaterial: It is currently in use.");
        // }

        masterMaterialRepository.deleteById(id);
    }
}