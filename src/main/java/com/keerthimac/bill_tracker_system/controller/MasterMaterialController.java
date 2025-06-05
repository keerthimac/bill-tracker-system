package com.keerthimac.bill_tracker_system.controller;

import com.keerthimac.bill_tracker_system.dto.MasterMaterialRequestDTO;
import com.keerthimac.bill_tracker_system.dto.MasterMaterialResponseDTO;
import com.keerthimac.bill_tracker_system.service.MasterMaterialService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/master-materials")
public class MasterMaterialController {

    private final MasterMaterialService masterMaterialService;

    @Autowired
    public MasterMaterialController(MasterMaterialService masterMaterialService) {
        this.masterMaterialService = masterMaterialService;
    }

    // Create a new Master Material
    @PostMapping
    public ResponseEntity<MasterMaterialResponseDTO> createMasterMaterial(
            @Valid @RequestBody MasterMaterialRequestDTO requestDTO) {
        MasterMaterialResponseDTO createdMaterial = masterMaterialService.createMasterMaterial(requestDTO);
        return new ResponseEntity<>(createdMaterial, HttpStatus.CREATED);
    }

    // Get a Master Material by its ID
    @GetMapping("/{id}")
    public ResponseEntity<MasterMaterialResponseDTO> getMasterMaterialById(@PathVariable Long id) {
        Optional<MasterMaterialResponseDTO> materialDTO = masterMaterialService.getMasterMaterialById(id);
        return materialDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get a Master Material by its Material Code
    @GetMapping("/by-code/{materialCode}")
    public ResponseEntity<MasterMaterialResponseDTO> getMasterMaterialByCode(@PathVariable String materialCode) {
        Optional<MasterMaterialResponseDTO> materialDTO = masterMaterialService.getMasterMaterialByCode(materialCode);
        return materialDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get all Master Materials or search by name fragment
    @GetMapping
    public ResponseEntity<List<MasterMaterialResponseDTO>> getAllOrSearchMasterMaterials(
            @RequestParam(name = "nameFragment", required = false) String nameFragment) {
        List<MasterMaterialResponseDTO> materials;
        if (nameFragment != null && !nameFragment.trim().isEmpty()) {
            materials = masterMaterialService.searchMasterMaterialsByName(nameFragment);
        } else {
            materials = masterMaterialService.getAllMasterMaterials();
        }

        if (materials.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(materials);
    }

    // Get Master Materials by Item Category ID
    @GetMapping("/by-category/{categoryId}")
    public ResponseEntity<List<MasterMaterialResponseDTO>> getMasterMaterialsByCategory(@PathVariable Long categoryId) {
        List<MasterMaterialResponseDTO> materials = masterMaterialService.getMasterMaterialsByCategory(categoryId);
        if (materials.isEmpty()) {
            // Even if category exists but has no materials, 204 is appropriate.
            // Service throws ResourceNotFound if category itself doesn't exist.
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(materials);
    }

    // Update an existing Master Material
    @PutMapping("/{id}")
    public ResponseEntity<MasterMaterialResponseDTO> updateMasterMaterial(
            @PathVariable Long id,
            @Valid @RequestBody MasterMaterialRequestDTO requestDTO) {
        // Service will throw ResourceNotFoundException if material with 'id' doesn't exist
        MasterMaterialResponseDTO updatedMaterial = masterMaterialService.updateMasterMaterial(id, requestDTO);
        return ResponseEntity.ok(updatedMaterial);
    }

    // Delete a Master Material by its ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMasterMaterial(@PathVariable Long id) {
        // Service will throw ResourceNotFoundException or DataIntegrityViolationException (or similar)
        masterMaterialService.deleteMasterMaterial(id);
        return ResponseEntity.noContent().build();
    }
}