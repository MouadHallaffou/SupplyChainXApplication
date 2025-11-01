package com.supplychainx.service_approvisionnement.service;

import com.supplychainx.service_approvisionnement.dto.CommandeFournisseurResponseDTO;
import com.supplychainx.service_approvisionnement.dto.CommandeFournisseurRequestDTO;
import com.supplychainx.service_approvisionnement.mapper.CommandeFournisseurMapper;
import com.supplychainx.service_approvisionnement.mapper.CommandeFournisseurMatiereMapper;
import com.supplychainx.service_approvisionnement.model.CommandeFournisseur;
import com.supplychainx.service_approvisionnement.model.CommandeFournisseurMatiere;
import com.supplychainx.service_approvisionnement.repository.CommandeFournisseurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommandeFournisseurServiceImpl implements CommandeFournisseurService {
    private final CommandeFournisseurMapper commandeFournisseurMapper;
    private final CommandeFournisseurMatiereMapper commandeFournisseurMatiereMapper;
    private final CommandeFournisseurRepository commandeFournisseurRepository;

    @Override
    @Transactional
    public CommandeFournisseurResponseDTO create(CommandeFournisseurRequestDTO commandeFournisseurRequestDTO) {
        CommandeFournisseur commandeFournisseur = commandeFournisseurMapper.toEntity(commandeFournisseurRequestDTO);
        List<CommandeFournisseurMatiere> matiereList = commandeFournisseurRequestDTO.getCommandeFournisseurMatieres()
                .stream()
                .map(commandeFournisseurMatiereMapper::toEntity)
                .collect(Collectors.toList());
        for (CommandeFournisseurMatiere matiere : matiereList) {
            matiere.setCommandeFournisseur(commandeFournisseur);
        }
        commandeFournisseur.setCommandeFournisseurMatieres(matiereList);
        return commandeFournisseurMapper.toResponseDTO(commandeFournisseurRepository.save(commandeFournisseur));
    }

    @Override
    public CommandeFournisseurResponseDTO getById(Long id) {
        CommandeFournisseur commande = commandeFournisseurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande not found with id: " + id));
        return commandeFournisseurMapper.toResponseDTO(commande);
    }

    @Override
    @Transactional
    public CommandeFournisseurResponseDTO update(CommandeFournisseurRequestDTO commandeFournisseurRequestDTO, Long id) {
        CommandeFournisseur existingCommande = commandeFournisseurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande not found with id: " + id));
        commandeFournisseurMapper.updateEntityFromDTO(commandeFournisseurRequestDTO, existingCommande);
        List<CommandeFournisseurMatiere> matiereList = commandeFournisseurRequestDTO.getCommandeFournisseurMatieres()
                .stream()
                .map(commandeFournisseurMatiereMapper::toEntity)
                .collect(Collectors.toList());
        for (CommandeFournisseurMatiere matiere : matiereList) {
            matiere.setCommandeFournisseur(existingCommande);
        }
        existingCommande.setCommandeFournisseurMatieres(matiereList);
        existingCommande.setOrderFournisseurId(id);
        return commandeFournisseurMapper.toResponseDTO(commandeFournisseurRepository.save(existingCommande));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!commandeFournisseurRepository.existsById(id)) {
            throw new RuntimeException("Commande not found with id: " + id);
        }
        commandeFournisseurRepository.deleteById(id);
    }

  @Override
  public Page<CommandeFournisseurResponseDTO> getAllCommandeFournisseurs(int page, int size) {
      Pageable pageable = Pageable.ofSize(size).withPage(page);
      Page<CommandeFournisseur> commandeFournisseurPage = commandeFournisseurRepository.findAll(pageable);
      List<CommandeFournisseurResponseDTO> dtoList = commandeFournisseurPage
              .map(commandeFournisseurMapper::toResponseDTO)
              .getContent();
      return new PageImpl<>(dtoList, commandeFournisseurPage.getPageable(), commandeFournisseurPage.getTotalElements());
  }

}
