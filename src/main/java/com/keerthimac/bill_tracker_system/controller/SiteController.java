package com.keerthimac.bill_tracker_system.controller;

import com.keerthimac.bill_tracker_system.dto.SiteDTO;
import com.keerthimac.bill_tracker_system.service.SiteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/sites") // Base path for all site-related APIs
public class SiteController {

    private final SiteService siteService;

    @Autowired
    public SiteController(SiteService siteService) {
        this.siteService = siteService;
    }

    // POST: Create a new Site
    @PostMapping
    public ResponseEntity<SiteDTO> createSite(@Valid @RequestBody SiteDTO siteDTO) { // <<<< @Valid IS CRUCIAL
        SiteDTO createdSite = siteService.createSite(siteDTO);
        return new ResponseEntity<>(createdSite, HttpStatus.CREATED);
    }

    // GET: Retrieve a Site by its ID
    @GetMapping("/{id}")
    public ResponseEntity<SiteDTO> getSiteById(@PathVariable Long id) {
        Optional<SiteDTO> siteDTO = siteService.getSiteById(id);
        return siteDTO.map(ResponseEntity::ok) // If present, return 200 OK
                .orElseGet(() -> ResponseEntity.notFound().build()); // If not, return 404 Not Found
    }

    // GET: Retrieve all Sites
    @GetMapping
    public ResponseEntity<List<SiteDTO>> getAllSites() {
        List<SiteDTO> sites = siteService.getAllSites();
        if (sites.isEmpty()) {
            return ResponseEntity.noContent().build(); // Return 204 No Content if list is empty
        }
        return ResponseEntity.ok(sites);
    }

    // PUT: Update an existing Site
    @PutMapping("/{id}")
    public ResponseEntity<SiteDTO> updateSite(
            @PathVariable Long id,
            @Valid @RequestBody SiteDTO siteDTO) {
        // The service layer should handle ResourceNotFoundException if ID doesn't exist
        SiteDTO updatedSite = siteService.updateSite(id, siteDTO);
        return ResponseEntity.ok(updatedSite);
    }

    // DELETE: Delete a Site by its ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSite(@PathVariable Long id) {
        // The service layer should handle ResourceNotFoundException if ID doesn't exist
        // It might also handle logic if a site is in use and cannot be deleted
        siteService.deleteSite(id);
        return ResponseEntity.noContent().build(); // Return 204 No Content on successful deletion
    }
}
