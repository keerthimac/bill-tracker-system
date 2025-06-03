package com.keerthimac.bill_tracker_system.service;

import com.keerthimac.bill_tracker_system.dto.SiteDTO;

import java.util.List;
import java.util.Optional;

public interface SiteService {
    SiteDTO createSite(SiteDTO siteDTO);
    Optional<SiteDTO> getSiteById(Long id);
    List<SiteDTO> getAllSites();
    SiteDTO updateSite(Long id, SiteDTO siteDTO);
    void deleteSite(Long id);
}
