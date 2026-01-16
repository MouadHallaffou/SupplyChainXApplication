package com.supplychainx.service_approvisionnement.controller;

import com.supplychainx.handler.GlobalSuccessHandler;
import com.supplychainx.service_approvisionnement.dto.Request.FournisseurRequestDTO;
import com.supplychainx.service_approvisionnement.dto.Response.FournisseurResponseDTO;
import com.supplychainx.service_approvisionnement.service.FournisseurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fournisseurs")
public class FournisseurController {
    private final FournisseurService fournisseurService;

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISEUR_LOGISTIQUE', 'GRESPONSABLE_ACHATS', 'GESTIONNAIRE_APPROVISIONNEMENT')")
    @GetMapping
    public List<FournisseurResponseDTO> getAllFournisseurs() {
        return fournisseurService.getAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISEUR_LOGISTIQUE', 'GRESPONSABLE_ACHATS', 'GESTIONNAIRE_APPROVISIONNEMENT')")
    @PostMapping
    public FournisseurResponseDTO createFournisseur(@Valid @RequestBody FournisseurRequestDTO fournisseurDTO) {
        return fournisseurService.create(fournisseurDTO);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISEUR_LOGISTIQUE', 'GRESPONSABLE_ACHATS', 'GESTIONNAIRE_APPROVISIONNEMENT')")
    @GetMapping("/{id}")
    public FournisseurResponseDTO getFournisseurById(@PathVariable("id") Long id) {
        return fournisseurService.getById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISEUR_LOGISTIQUE', 'GRESPONSABLE_ACHATS', 'GESTIONNAIRE_APPROVISIONNEMENT')")
    @PutMapping("/{id}")
    public FournisseurResponseDTO updateFournisseur(@PathVariable("id") Long id,
                                                    @Valid @RequestBody FournisseurRequestDTO fournisseurDTO) {
        return fournisseurService.update(fournisseurDTO, id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISEUR_LOGISTIQUE', 'GRESPONSABLE_ACHATS', 'GESTIONNAIRE_APPROVISIONNEMENT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteFournisseur(@PathVariable("id") Long id) {
        fournisseurService.delete(id);
        return GlobalSuccessHandler.handleDeleted("Fournisseur deleted successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISEUR_LOGISTIQUE', 'GRESPONSABLE_ACHATS', 'GESTIONNAIRE_APPROVISIONNEMENT')")
    @GetMapping("/search")
    public FournisseurResponseDTO searchFournisseurByName(@RequestParam("name") String name) {
        return fournisseurService.searchByName(name);
    }

}