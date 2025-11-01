package com.supplychainx.service_approvisionnement.service;

import com.supplychainx.service_approvisionnement.dto.CommandeFournisseurResponseDTO;
import com.supplychainx.service_approvisionnement.dto.CommandeFournisseurRequestDTO;
import org.springframework.data.domain.Page;


public interface CommandeFournisseurService {
    CommandeFournisseurResponseDTO create(CommandeFournisseurRequestDTO fournisseur);

    CommandeFournisseurResponseDTO getById(Long id);

    CommandeFournisseurResponseDTO update(CommandeFournisseurRequestDTO fournisseur , Long id);

    void delete(Long id);

    Page<CommandeFournisseurResponseDTO> getAllCommandeFournisseurs(int page, int size);
}
