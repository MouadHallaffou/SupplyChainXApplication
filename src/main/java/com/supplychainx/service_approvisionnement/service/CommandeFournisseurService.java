package com.supplychainx.service_approvisionnement.service;

import com.supplychainx.service_approvisionnement.dto.Response.CommandeFournisseurResponseDTO;
import com.supplychainx.service_approvisionnement.dto.Request.CommandeFournisseurRequestDTO;
import org.springframework.data.domain.Page;


public interface CommandeFournisseurService {
    CommandeFournisseurResponseDTO create(CommandeFournisseurRequestDTO fournisseur);
    CommandeFournisseurResponseDTO getById(Long id);
    CommandeFournisseurResponseDTO update(CommandeFournisseurRequestDTO fournisseur , Long id);
    void delete(Long id);
    Page<CommandeFournisseurResponseDTO> getAllCommandeFournisseurs(int page, int size);
    CommandeFournisseurResponseDTO startProductionOrder (Long id);
    CommandeFournisseurResponseDTO completeProductionOrder (Long id);
    CommandeFournisseurResponseDTO cancelProductionOrder (Long id);
}
