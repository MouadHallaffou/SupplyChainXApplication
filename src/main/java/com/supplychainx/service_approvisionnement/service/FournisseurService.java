package com.supplychainx.service_approvisionnement.service;

import com.supplychainx.service_approvisionnement.dto.Request.FournisseurRequestDTO;
import com.supplychainx.service_approvisionnement.dto.Response.FournisseurResponseDTO;

import java.util.List;

public interface FournisseurService {
    FournisseurResponseDTO create(FournisseurRequestDTO fournisseurRequestDTO);
    FournisseurResponseDTO getById(Long id);
    FournisseurResponseDTO update(FournisseurRequestDTO fournisseurRequestDTO, Long id);
    void delete(Long id);
    List<FournisseurResponseDTO> getAll();
    FournisseurResponseDTO searchByName(String name);
}
