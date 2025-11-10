package com.supplychainx.service_livraison.service;

import com.supplychainx.service_livraison.dto.Request.LivraisonRequestDTO;
import com.supplychainx.service_livraison.dto.Response.LivraisonResponseDTO;
import org.springframework.data.domain.Page;

public interface LivraisonService {
    LivraisonResponseDTO createLivraison(LivraisonRequestDTO dto);
    LivraisonResponseDTO getLivraisonById(Long id);
    LivraisonResponseDTO updateLivraison(Long id, LivraisonRequestDTO dto);
    void deleteLivraison(Long id);
    Page<LivraisonResponseDTO> getAllLivraisons(int page, int size , String sortBy, String sortDir);
    LivraisonResponseDTO updateLivraisonStatus(Long id, String status);
}
