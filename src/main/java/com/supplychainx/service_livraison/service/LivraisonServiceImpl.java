package com.supplychainx.service_livraison.service;

import com.supplychainx.exception.ResourceNotFoundException;
import com.supplychainx.service_livraison.dto.Request.LivraisonRequestDTO;
import com.supplychainx.service_livraison.dto.Response.LivraisonResponseDTO;
import com.supplychainx.service_livraison.mapper.LivraisonMapper;
import com.supplychainx.service_livraison.model.Livraison;
import com.supplychainx.service_livraison.model.enums.LivraisonStatus;
import com.supplychainx.service_livraison.repository.LivraisonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LivraisonServiceImpl implements LivraisonService {

    private final LivraisonMapper livraisonMapper;
    private final LivraisonRepository livraisonRepository;

    @Override
    @Transactional
    public LivraisonResponseDTO createLivraison(LivraisonRequestDTO dto) {
        if (dto.getClientOrderId() == null) {
            throw new ResourceNotFoundException("Client Order ID cannot be null");
        }
        if (livraisonRepository.existsById(dto.getClientOrderId())) {
            throw new ResourceNotFoundException("A livraison with the given Client Order ID already exists");
        }
        Livraison livraison = livraisonMapper.toEntity(dto);
        if (dto.getStatus() == null) {
            livraison.setStatus(LivraisonStatus.PLANNIFIEE);
        }
        Livraison savedLivraison = livraisonRepository.save(livraison);
        return livraisonMapper.toResponseDTO(savedLivraison);
    }

    @Override
    @Transactional(readOnly = true)
    public LivraisonResponseDTO getLivraisonById(Long id) {
        Livraison livraison = livraisonRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Livraison not found with id: " + id));
        return livraisonMapper.toResponseDTO(livraison);
    }

    @Override
    @Transactional
    public LivraisonResponseDTO updateLivraison(Long id, LivraisonRequestDTO dto) {
        Livraison existingLivraison = livraisonRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Livraison not found with id: " + id));
        livraisonMapper.toUpdateResponseDTO(dto, existingLivraison);
        existingLivraison.setLivraisonId(id);
        Livraison updatedLivraison = livraisonRepository.save(existingLivraison);
        return livraisonMapper.toResponseDTO(updatedLivraison);
    }

    @Override
    @Transactional
    public void deleteLivraison(Long id) {
        getLivraisonById(id);
        livraisonRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LivraisonResponseDTO> getAllLivraisons(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return livraisonRepository.findAll(org.springframework.data.domain.PageRequest.of(page, size, sort))
                .map(livraisonMapper::toResponseDTO);
    }

    @Override
    @Transactional
    public LivraisonResponseDTO updateLivraisonStatus(Long id, String status) {
        Livraison existingLivraison = livraisonRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Livraison not found with id: " + id));
        existingLivraison.setStatus(LivraisonStatus.valueOf(status));
        existingLivraison.setLivraisonId(id);
        Livraison updatedLivraison = livraisonRepository.save(existingLivraison);
        return livraisonMapper.toResponseDTO(updatedLivraison);
    }

}
