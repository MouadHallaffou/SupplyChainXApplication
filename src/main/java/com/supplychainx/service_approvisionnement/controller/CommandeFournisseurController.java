package com.supplychainx.service_approvisionnement.controller;

import com.supplychainx.handler.GlobalSuccessHandler;
import com.supplychainx.service_approvisionnement.dto.Request.CommandeFournisseurRequestDTO;
import com.supplychainx.service_approvisionnement.dto.Response.CommandeFournisseurResponseDTO;
import com.supplychainx.service_approvisionnement.service.CommandeFournisseurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/commande-fournisseurs")
public class CommandeFournisseurController {
    private final CommandeFournisseurService commandeFournisseurService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('APPROVISIONNEMENT_MANAGER')")
    @PostMapping
    public ResponseEntity<CommandeFournisseurResponseDTO> createCommandeFournisseur(@Valid @RequestBody CommandeFournisseurRequestDTO commandeFournisseurRequestDTO) {
        CommandeFournisseurResponseDTO responseDTO = commandeFournisseurService.create(commandeFournisseurRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<Page<CommandeFournisseurResponseDTO>> getAllCommandeFournisseurMatieres(int page, int size) {
        Page<CommandeFournisseurResponseDTO> matieres = commandeFournisseurService.getAllCommandeFournisseurs(page, size);
        return ResponseEntity.ok(matieres);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommandeFournisseurResponseDTO> getCommandeFournisseurById(@PathVariable("id") Long id) {
        CommandeFournisseurResponseDTO responseDTO = commandeFournisseurService.getById(id);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommandeFournisseurResponseDTO> updateCommandeFournisseur(@Valid @PathVariable("id") Long id, @RequestBody CommandeFournisseurRequestDTO commandeFournisseurRequestDTO) {
        CommandeFournisseurResponseDTO responseDTO = commandeFournisseurService.update(commandeFournisseurRequestDTO, id);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCommandeFournisseur(@PathVariable("id") Long id) {
        commandeFournisseurService.delete(id);
        return GlobalSuccessHandler.handleDeleted("Commande fournisseur deleted successfully");
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<Map<String, Object>> startCommandeFournisseur(@PathVariable("id") Long id) {
        CommandeFournisseurResponseDTO responseDTO = commandeFournisseurService.startProductionOrder(id);
        return GlobalSuccessHandler.handleSuccessWithData("Commande fournisseur started successfully", responseDTO);
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<Map<String, Object>> completeCommandeFournisseur(@PathVariable("id") Long id) {
        CommandeFournisseurResponseDTO responseDTO = commandeFournisseurService.completeProductionOrder(id);
        return GlobalSuccessHandler.handleSuccessWithData("Commande fournisseur completed successfully", responseDTO);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Map<String, Object>> cancelCommandeFournisseur(@PathVariable("id") Long id) {
        CommandeFournisseurResponseDTO responseDTO = commandeFournisseurService.cancelProductionOrder(id);
        return GlobalSuccessHandler.handleSuccessWithData("Commande fournisseur canceled successfully", responseDTO);
    }

}
