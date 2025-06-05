package com.keerthimac.bill_tracker_system.service.impl;

import com.keerthimac.bill_tracker_system.dto.BrandRequestDTO;
import com.keerthimac.bill_tracker_system.dto.BrandResponseDTO;
import com.keerthimac.bill_tracker_system.exception.DuplicateResourceException;
import com.keerthimac.bill_tracker_system.exception.ResourceNotFoundException;
import com.keerthimac.bill_tracker_system.exception.ResourceInUseException; // New custom exception
import com.keerthimac.bill_tracker_system.mapper.BrandMapper;
import com.keerthimac.bill_tracker_system.entity.Brand;
import com.keerthimac.bill_tracker_system.repository.BrandRepository;
import com.keerthimac.bill_tracker_system.repository.MasterMaterialRepository; // To check usage
import com.keerthimac.bill_tracker_system.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;
    private final MasterMaterialRepository masterMaterialRepository; // Injected for delete check

    @Autowired
    public BrandServiceImpl(BrandRepository brandRepository,
                            BrandMapper brandMapper,
                            MasterMaterialRepository masterMaterialRepository) {
        this.brandRepository = brandRepository;
        this.brandMapper = brandMapper;
        this.masterMaterialRepository = masterMaterialRepository;
    }

    @Override
    @Transactional
    public BrandResponseDTO createBrand(BrandRequestDTO requestDTO) {
        if (brandRepository.existsByNameIgnoreCase(requestDTO.getName())) {
            throw new DuplicateResourceException("Brand with name '" + requestDTO.getName() + "' already exists.");
        }

        Brand brand = brandMapper.toEntity(requestDTO);
        // brandImagePath is mapped directly by mapper if present in DTO
        // Timestamps are auto-generated

        Brand savedBrand = brandRepository.save(brand);
        return brandMapper.toDto(savedBrand);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BrandResponseDTO> getBrandById(Long id) {
        return brandRepository.findById(id).map(brandMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BrandResponseDTO> getBrandByName(String name) {
        return brandRepository.findByNameIgnoreCase(name).map(brandMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandResponseDTO> getAllBrands() {
        return brandMapper.toDtoList(brandRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandResponseDTO> searchBrandsByName(String nameFragment) {
        if (!StringUtils.hasText(nameFragment)) {
            return getAllBrands(); // Or return empty list if no fragment means no search
        }
        return brandMapper.toDtoList(brandRepository.findByNameContainingIgnoreCase(nameFragment));
    }

    @Override
    @Transactional
    public BrandResponseDTO updateBrand(Long id, BrandRequestDTO requestDTO) {
        Brand existingBrand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + id));

        // Check if the new name conflicts with another existing brand
        if (!existingBrand.getName().equalsIgnoreCase(requestDTO.getName()) &&
                brandRepository.existsByNameIgnoreCase(requestDTO.getName())) {
            throw new DuplicateResourceException("Another brand with name '" + requestDTO.getName() + "' already exists.");
        }

        existingBrand.setName(requestDTO.getName());
        existingBrand.setDescription(requestDTO.getDescription());
        existingBrand.setBrandImagePath(requestDTO.getBrandImagePath()); // Update image path
        // updatedAt will be automatically handled by @UpdateTimestamp

        Brand updatedBrand = brandRepository.save(existingBrand);
        return brandMapper.toDto(updatedBrand);
    }

    @Override
    @Transactional
    public BrandResponseDTO updateBrandImagePath(Long brandId, String imagePath) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + brandId));
        brand.setBrandImagePath(imagePath);
        Brand updatedBrand = brandRepository.save(brand);
        return brandMapper.toDto(updatedBrand);
    }

    @Override
    @Transactional
    public void deleteBrand(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + id + ". Cannot delete."));

        // Check if the brand is used in any MasterMaterial
        if (masterMaterialRepository.existsByBrand_Id(id)) {
            throw new ResourceInUseException("Cannot delete Brand: '" + brand.getName() + "' as it is currently associated with master materials.");
        }

        brandRepository.deleteById(id);
    }
}
