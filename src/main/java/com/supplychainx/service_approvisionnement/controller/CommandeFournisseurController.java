package com.supplychainx.service_approvisionnement.controller;

import com.supplychainx.handler.GlobalSuccessHandler;
import com.supplychainx.service_approvisionnement.dto.CommandeFournisseurRequestDTO;
import com.supplychainx.service_approvisionnement.dto.CommandeFournisseurResponseDTO;
import com.supplychainx.service_approvisionnement.service.CommandeFournisseurService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/commande-fournisseurs")
public class CommandeFournisseurController {
    private final CommandeFournisseurService commandeFournisseurService;

    public CommandeFournisseurController(CommandeFournisseurService commandeFournisseurService) {
        this.commandeFournisseurService = commandeFournisseurService;
    }

    @PostMapping
    public ResponseEntity<CommandeFournisseurResponseDTO> createCommandeFournisseur(@RequestBody CommandeFournisseurRequestDTO commandeFournisseurRequestDTO) {
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
    public ResponseEntity<CommandeFournisseurResponseDTO> updateCommandeFournisseur(@PathVariable("id") Long id, @RequestBody CommandeFournisseurRequestDTO commandeFournisseurRequestDTO) {
        System.out.println("Updating CommandeFournisseur with ID: " + id);
        System.out.println("Request Data: " + commandeFournisseurRequestDTO);
        CommandeFournisseurResponseDTO responseDTO = commandeFournisseurService.update(commandeFournisseurRequestDTO, id);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCommandeFournisseur(@PathVariable("id") Long id) {
        commandeFournisseurService.delete(id);
        return GlobalSuccessHandler.handleDeleted("Commande fournisseur deleted successfully");
    }

}
