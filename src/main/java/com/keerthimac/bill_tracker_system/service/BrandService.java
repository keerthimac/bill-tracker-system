package com.keerthimac.bill_tracker_system.service;

import com.keerthimac.bill_tracker_system.dto.BrandRequestDTO;
import com.keerthimac.bill_tracker_system.dto.BrandResponseDTO;

import java.util.List;
import java.util.Optional;

public interface BrandService {

    BrandResponseDTO createBrand(BrandRequestDTO requestDTO);

    Optional<BrandResponseDTO> getBrandById(Long id);

    Optional<BrandResponseDTO> getBrandByName(String name);

    List<BrandResponseDTO> getAllBrands();

    List<BrandResponseDTO> searchBrandsByName(String nameFragment);

    BrandResponseDTO updateBrand(Long id, BrandRequestDTO requestDTO);

    /**
     * Updates only the image path for a given brand.
     * This will be used by the file upload process later.
     * @param brandId The ID of the brand to update.
     * @param imagePath The new image path/URL.
     * @return The updated brand.
     */
    BrandResponseDTO updateBrandImagePath(Long brandId, String imagePath);

    void deleteBrand(Long id);
}