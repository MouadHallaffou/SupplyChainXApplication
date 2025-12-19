package com.supplychainx.service_approvisionnement.controller;

import com.supplychainx.handler.GlobalSuccessHandler;
import com.supplychainx.service_approvisionnement.dto.Request.MatierePremiereRequestDTO;
import com.supplychainx.service_approvisionnement.dto.Response.MatierePremiereResponseDTO;
import com.supplychainx.service_approvisionnement.service.MatierePremiereService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/matieres-premieres")
public class MatierePremiereController {
    private final MatierePremiereService matierePremiereService;

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISEUR_LOGISTIQUE', 'GRESPONSABLE_ACHATS', 'GESTIONNAIRE_APPROVISIONNEMENT')")
    @GetMapping
    public Page<MatierePremiereResponseDTO> getAllMatieresPremieres(@RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size) {
        return matierePremiereService.getAll(page, size);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISEUR_LOGISTIQUE', 'GRESPONSABLE_ACHATS', 'GESTIONNAIRE_APPROVISIONNEMENT')")
    @PostMapping
    public ResponseEntity<Map<String, Object>> createMatierePremiere(@Valid @RequestBody MatierePremiereRequestDTO matierePremiereRequestDTO) {
        MatierePremiereResponseDTO responseDTO = matierePremiereService.create(matierePremiereRequestDTO);
        return GlobalSuccessHandler.handleSuccessWithData("Matière premiere created successfully", responseDTO);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISEUR_LOGISTIQUE', 'GRESPONSABLE_ACHATS', 'GESTIONNAIRE_APPROVISIONNEMENT')")
    @GetMapping("/{id}")
    public MatierePremiereResponseDTO getMatierePremiereById(@PathVariable("id") Long id) {
        return matierePremiereService.getById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISEUR_LOGISTIQUE', 'GRESPONSABLE_ACHATS', 'GESTIONNAIRE_APPROVISIONNEMENT')")
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateMatierePremiere(@PathVariable("id") Long id,
                                                                     @Valid @RequestBody MatierePremiereRequestDTO matierePremiereRequestDTO) {
        MatierePremiereResponseDTO responseDTO = matierePremiereService.update(matierePremiereRequestDTO, id);
        return GlobalSuccessHandler.handleSuccessWithData("Matière premiere updated successfully", responseDTO);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISEUR_LOGISTIQUE', 'GRESPONSABLE_ACHATS', 'GESTIONNAIRE_APPROVISIONNEMENT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteMatierePremiere(@PathVariable("id") Long id) {
        matierePremiereService.delete(id);
        return GlobalSuccessHandler.handleDeleted("Matière premiere deleted successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISEUR_LOGISTIQUE', 'GRESPONSABLE_ACHATS', 'GESTIONNAIRE_APPROVISIONNEMENT')")
    @GetMapping("/filtrer-par-stock-critique")
    public Page<MatierePremiereResponseDTO> filtrerParStockCritique(@RequestParam int stockCritique,
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size) {
        return matierePremiereService.filtrerParStockCritique(stockCritique, page, size);
    }

}