package com.supplychainx.service_approvisionnement.service;

import com.supplychainx.exception.ResourceNotFoundException;
import com.supplychainx.service_approvisionnement.dto.MatierePremiereRequestDTO;
import com.supplychainx.service_approvisionnement.dto.MatierePremiereResponseDTO;
import com.supplychainx.service_approvisionnement.mapper.MatierePremiereMapper;
import com.supplychainx.service_approvisionnement.model.Fournisseur;
import com.supplychainx.service_approvisionnement.model.MatierePremiere;
import com.supplychainx.service_approvisionnement.repository.FournisseurRepository;
import com.supplychainx.service_approvisionnement.repository.MatierePremiereRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@Transactional
@RequiredArgsConstructor
public class MatierePremiereServiceImpl implements MatierePremiereService {
    private final MatierePremiereRepository matierePremiereRepository;
    private final MatierePremiereMapper matierePremiereMapper;
    private final FournisseurRepository fournisseurRepository;

    @Override
    public MatierePremiereResponseDTO create(MatierePremiereRequestDTO dto) {
        if (dto == null) {
            throw new ResourceNotFoundException("MatierePremiereRequestDTO object is null");
        }
        MatierePremiere matiere = matierePremiereMapper.toEntity(dto);
        for (Long fournisseurId : dto.getFournisseurIds()) {
            Fournisseur fournisseur = fournisseurRepository.findById(fournisseurId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Fournisseur not found with id: " + fournisseurId));
            // Vérifier doublon pour ce fournisseur
            boolean exists = fournisseur.getMatieresPremieres().stream()
                    .anyMatch(m -> m.getName().equalsIgnoreCase(dto.getName()));
            if (exists) {
                throw new ResourceNotFoundException(
                        "MatierePremiere with name '" + dto.getName() + "' already exists for fournisseur " + fournisseur.getName());
            }
            matiere.getFournisseurs().add(fournisseur);
            fournisseur.getMatieresPremieres().add(matiere);
            fournisseurRepository.save(fournisseur);
        }
        MatierePremiere saved = matierePremiereRepository.save(matiere);
        return matierePremiereMapper.toResponseDTO(saved);
    }

    @Override
    public MatierePremiereResponseDTO getById(Long id) {
        MatierePremiere matierePremiere = matierePremiereRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MatierePremiere not found with id: " + id));
        return matierePremiereMapper.toResponseDTO(matierePremiere);
    }

    @Override
    public MatierePremiereResponseDTO update(MatierePremiereRequestDTO matierePremiereRequestDTO, Long id) {
        MatierePremiere existingMatierePremiere = matierePremiereRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MatierePremiere not found with id: " + id));
        matierePremiereMapper.updateEntityFromDTO(matierePremiereRequestDTO, existingMatierePremiere);
        // Update fournisseurs
        existingMatierePremiere.getFournisseurs().clear();
        for (Long fournisseurId : matierePremiereRequestDTO.getFournisseurIds()) {
            Fournisseur fournisseur = fournisseurRepository.findById(fournisseurId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Fournisseur not found with id: " + fournisseurId));
            existingMatierePremiere.getFournisseurs().add(fournisseur);
            if (!fournisseur.getMatieresPremieres().contains(existingMatierePremiere)) {
                fournisseur.getMatieresPremieres().add(existingMatierePremiere);
            }
            fournisseurRepository.save(fournisseur);
        }
        MatierePremiere updatedMatierePremiere = matierePremiereRepository.save(existingMatierePremiere);
        return matierePremiereMapper.toResponseDTO(updatedMatierePremiere);
    }

    @Override
    public void delete(Long id) {
        if (!matierePremiereRepository.existsById(id)) {
            throw new ResourceNotFoundException("MatierePremiere not found with id: " + id);
        }
        matierePremiereRepository.deleteById(id);
    }

    @Override
    public Page<MatierePremiereResponseDTO> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MatierePremiere> matierePremierePage = matierePremiereRepository.findAll(pageable);
        return matierePremierePage.map(matierePremiereMapper::toResponseDTO);
    }

}