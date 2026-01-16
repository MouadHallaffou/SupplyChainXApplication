package com.supplychainx.service_approvisionnement.service;

import com.supplychainx.service_approvisionnement.dto.Request.MatierePremiereRequestDTO;
import com.supplychainx.service_approvisionnement.dto.Response.MatierePremiereResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MatierePremiereService {
    MatierePremiereResponseDTO create(MatierePremiereRequestDTO matierePremiereRequestDTO);
    MatierePremiereResponseDTO getById(Long id);
    MatierePremiereResponseDTO update(MatierePremiereRequestDTO matierePremiereRequestDTO, Long id);
    void delete(Long id);
    Page<MatierePremiereResponseDTO> getAll(int page, int size);
    Page<MatierePremiereResponseDTO> filtrerParStockCritique(int stockCritique, int page, int size);
    List<MatierePremiereResponseDTO> getMatieresByFournisseurId(Long fournisseurId);
}