package com.supplychainx.service_approvisionnement.service;

import com.supplychainx.service_approvisionnement.dto.MatierePremiereRequestDTO;
import com.supplychainx.service_approvisionnement.dto.MatierePremiereResponseDTO;
import org.springframework.data.domain.Page;

public interface MatierePremiereService {
    MatierePremiereResponseDTO create(MatierePremiereRequestDTO matierePremiereRequestDTO);
    MatierePremiereResponseDTO getById(Long id);
    MatierePremiereResponseDTO update(MatierePremiereRequestDTO matierePremiereRequestDTO, Long id);
    void delete(Long id);
    Page<MatierePremiereResponseDTO> getAll(int page, int size);
}