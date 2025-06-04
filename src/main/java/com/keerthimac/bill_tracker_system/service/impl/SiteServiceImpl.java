package com.keerthimac.bill_tracker_system.service.impl;

import com.keerthimac.bill_tracker_system.dto.SiteDTO;
import com.keerthimac.bill_tracker_system.entity.Site; // Your entity
import com.keerthimac.bill_tracker_system.exception.DuplicateResourceException; // Your custom exception
import com.keerthimac.bill_tracker_system.exception.ResourceNotFoundException; // Your custom exception
import com.keerthimac.bill_tracker_system.mapper.SiteMapper; // Your mapper
import com.keerthimac.bill_tracker_system.repository.SiteRepository; // Your repository
import com.keerthimac.bill_tracker_system.service.SiteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service; // <<<< THIS IS THE CRUCIAL ANNOTATION
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service // <<<< ENSURE THIS @Service ANNOTATION IS PRESENT
public class SiteServiceImpl implements SiteService {

    private final SiteRepository siteRepository;
    private final SiteMapper siteMapper;

    @Autowired
    public SiteServiceImpl(SiteRepository siteRepository, SiteMapper siteMapper) {
        this.siteRepository = siteRepository;
        this.siteMapper = siteMapper;
    }

    @Override
    @Transactional
    public SiteDTO createSite(SiteDTO siteDTO) {
        // Optional: Check for duplicate site name if names should be unique
        siteRepository.findByNameIgnoreCase(siteDTO.getName()).ifPresent(existingSite -> {
            throw new DuplicateResourceException("Site with name '" + siteDTO.getName() + "' already exists.");
        });

        Site site = siteMapper.toEntity(siteDTO);
        Site savedSite = siteRepository.save(site);
        return siteMapper.toDto(savedSite);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SiteDTO> getSiteById(Long id) {
        return siteRepository.findById(id)
                .map(siteMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SiteDTO> getAllSites() {
        List<Site> sites = siteRepository.findAll();
        // Ensure SiteMapper has toDtoList. If not, add: List<SiteDTO> toDtoList(List<Site> sites);
        return siteMapper.toDtoList(sites);
    }

    @Override
    @Transactional
    public SiteDTO updateSite(Long id, SiteDTO siteDTO) {
        Site existingSite = siteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Site not found with id: " + id));

        // Optional: Check if new name conflicts with another existing site
        if (siteDTO.getName() != null && !existingSite.getName().equalsIgnoreCase(siteDTO.getName())) {
            siteRepository.findByNameIgnoreCase(siteDTO.getName()).ifPresent(conflictingSite -> {
                if (!conflictingSite.getId().equals(id)) { // If it's a different site
                    throw new DuplicateResourceException("Another Site with name '" + siteDTO.getName() + "' already exists.");
                }
            });
        }

        // Update fields from DTO
        if (siteDTO.getName() != null) { // Only update if provided
            existingSite.setName(siteDTO.getName());
        }
        if (siteDTO.getLocation() != null) { // Only update if provided
            existingSite.setLocation(siteDTO.getLocation());
        }


        Site updatedSite = siteRepository.save(existingSite);
        return siteMapper.toDto(updatedSite);
    }

    @Override
    @Transactional
    public void deleteSite(Long id) {
        if (!siteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Site not found with id: " + id + ". Cannot delete.");
        }
        // TODO: Add checks here if the site is in use by PurchaseBills before deleting
        // Example: if (purchaseBillRepository.countBySiteId(id) > 0) { throw new ... }
        siteRepository.deleteById(id);
    }
}
