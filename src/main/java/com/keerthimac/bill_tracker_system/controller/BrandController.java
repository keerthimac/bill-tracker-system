package com.keerthimac.bill_tracker_system.controller;

import com.keerthimac.bill_tracker_system.dto.BrandRequestDTO;
import com.keerthimac.bill_tracker_system.dto.BrandResponseDTO;
import com.keerthimac.bill_tracker_system.dto.ImagePathUpdateRequestDTO; // New DTO
import com.keerthimac.bill_tracker_system.service.BrandService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// import org.springframework.web.multipart.MultipartFile; // For actual file upload later

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/brands")
public class BrandController {

    private final BrandService brandService;

    @Autowired
    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    // Create a new Brand
    @PostMapping
    public ResponseEntity<BrandResponseDTO> createBrand(@Valid @RequestBody BrandRequestDTO requestDTO) {
        BrandResponseDTO createdBrand = brandService.createBrand(requestDTO);
        return new ResponseEntity<>(createdBrand, HttpStatus.CREATED);
    }

    // Get a Brand by its ID
    @GetMapping("/{id}")
    public ResponseEntity<BrandResponseDTO> getBrandById(@PathVariable Long id) {
        Optional<BrandResponseDTO> brandDTO = brandService.getBrandById(id);
        return brandDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get a Brand by its name (case-insensitive)
    @GetMapping("/by-name")
    public ResponseEntity<BrandResponseDTO> getBrandByName(@RequestParam String name) {
        Optional<BrandResponseDTO> brandDTO = brandService.getBrandByName(name);
        return brandDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get all Brands or search by name fragment
    @GetMapping
    public ResponseEntity<List<BrandResponseDTO>> getAllOrSearchBrands(
            @RequestParam(name = "nameFragment", required = false) String nameFragment) {
        List<BrandResponseDTO> brands;
        if (nameFragment != null && !nameFragment.trim().isEmpty()) {
            brands = brandService.searchBrandsByName(nameFragment);
        } else {
            brands = brandService.getAllBrands();
        }

        if (brands.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(brands);
    }

    // Update an existing Brand (text details and image path if included in DTO)
    @PutMapping("/{id}")
    public ResponseEntity<BrandResponseDTO> updateBrand(
            @PathVariable Long id,
            @Valid @RequestBody BrandRequestDTO requestDTO) {
        BrandResponseDTO updatedBrand = brandService.updateBrand(id, requestDTO);
        return ResponseEntity.ok(updatedBrand);
    }

    // Specifically update the brand image path
    @PatchMapping("/{id}/image-path")
    public ResponseEntity<BrandResponseDTO> updateBrandImagePath(
            @PathVariable Long id,
            @Valid @RequestBody ImagePathUpdateRequestDTO imagePathDTO) {
        BrandResponseDTO updatedBrand = brandService.updateBrandImagePath(id, imagePathDTO.getImagePath());
        return ResponseEntity.ok(updatedBrand);
    }

    // TODO: Add a dedicated endpoint for ACTUAL IMAGE FILE UPLOAD
    // This would look something like:
    // @PostMapping("/{id}/image")
    // public ResponseEntity<BrandResponseDTO> uploadBrandImage(
    // @PathVariable Long id,
    // @RequestParam("file") MultipartFile file) {
    // // 1. Call FileStorageService to save the file and get its path
    // // String imagePath = fileStorageService.storeFile(file, "brands");
    // // 2. Call brandService.updateBrandImagePath(id, imagePath);
    // // return ResponseEntity.ok(updatedBrand);
    // return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); // Placeholder
    // }


    // Delete a Brand by its ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }
}