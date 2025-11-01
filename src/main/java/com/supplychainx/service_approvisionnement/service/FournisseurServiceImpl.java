package com.supplychainx.service_approvisionnement.service;

import com.supplychainx.exception.ResourceNotFoundException;
import com.supplychainx.service_approvisionnement.dto.FournisseurRequestDTO;
import com.supplychainx.service_approvisionnement.dto.FournisseurResponseDTO;
import com.supplychainx.service_approvisionnement.mapper.FournisseurMapper;
import com.supplychainx.service_approvisionnement.model.Fournisseur;
import com.supplychainx.service_approvisionnement.repository.FournisseurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FournisseurServiceImpl implements FournisseurService {
    private final FournisseurRepository fournisseurRepository;
    private final FournisseurMapper fournisseurMapper;

    @Override
    @Transactional
    public FournisseurResponseDTO create(FournisseurRequestDTO fournisseurRequestDTO) {
        Fournisseur fournisseur = fournisseurMapper.toEntity(fournisseurRequestDTO);
        Fournisseur savedFournisseur = fournisseurRepository.save(fournisseur);
        return fournisseurMapper.toResponseDTO(savedFournisseur);
    }

    @Override
    public FournisseurResponseDTO getById(Long id) {
        Fournisseur fournisseur = fournisseurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur not found " + id));
        return fournisseurMapper.toResponseDTO(fournisseur);
    }

    @Override
    @Transactional
    public FournisseurResponseDTO update(FournisseurRequestDTO fournisseurRequestDTO, Long id) {
        Fournisseur existingFournisseur = fournisseurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur not found " + id));

        // Mettre à jour l'entité existante avec les données du DTO
        fournisseurMapper.updateEntityFromDTO(fournisseurRequestDTO, existingFournisseur);
        existingFournisseur.setFournisseurId(id);

        Fournisseur updatedFournisseur = fournisseurRepository.save(existingFournisseur);
        return fournisseurMapper.toResponseDTO(updatedFournisseur);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!fournisseurRepository.existsById(id)) {
            throw new ResourceNotFoundException("Fournisseur not found " + id);
        }
        fournisseurRepository.deleteById(id);
    }

    @Override
    public List<FournisseurResponseDTO> getAll() {
        return fournisseurRepository.findAll()
                .stream()
                .map(fournisseurMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

}