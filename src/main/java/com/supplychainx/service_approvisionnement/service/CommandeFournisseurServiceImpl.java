package com.supplychainx.service_approvisionnement.service;

import com.supplychainx.exception.ResourceNotFoundException;
import com.supplychainx.service_approvisionnement.dto.Response.CommandeFournisseurResponseDTO;
import com.supplychainx.service_approvisionnement.dto.Request.CommandeFournisseurRequestDTO;
import com.supplychainx.service_approvisionnement.mapper.CommandeFournisseurMapper;
import com.supplychainx.service_approvisionnement.mapper.CommandeFournisseurMatiereMapper;
import com.supplychainx.service_approvisionnement.model.CommandeFournisseur;
import com.supplychainx.service_approvisionnement.model.CommandeFournisseurMatiere;
import com.supplychainx.service_approvisionnement.model.MatierePremiere;
import com.supplychainx.service_approvisionnement.model.enums.FournisseurOrderStatus;
import com.supplychainx.service_approvisionnement.repository.CommandeFournisseurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

        handleCommandeMatieresSafely(commandeFournisseurRequestDTO, commandeFournisseur);

        commandeFournisseur.setStatus(FournisseurOrderStatus.EN_ATTENTE);
        return commandeFournisseurMapper.toResponseDTO(commandeFournisseurRepository.save(commandeFournisseur));
    }

    @Override
    public CommandeFournisseurResponseDTO getById(Long id) {
        CommandeFournisseur commande = commandeFournisseurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande not found with id: " + id));
        return commandeFournisseurMapper.toResponseDTO(commande);
    }

    @Override
    @Transactional
    public CommandeFournisseurResponseDTO update(CommandeFournisseurRequestDTO commandeFournisseurRequestDTO, Long id) {
        CommandeFournisseur existingCommande = commandeFournisseurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande not found with id: " + id));
        commandeFournisseurMapper.updateEntityFromDTO(commandeFournisseurRequestDTO, existingCommande);

        handleCommandeMatieresSafely(commandeFournisseurRequestDTO, existingCommande);

        existingCommande.setOrderFournisseurId(id);
        return commandeFournisseurMapper.toResponseDTO(commandeFournisseurRepository.save(existingCommande));
    }

    private void handleCommandeMatieresSafely(CommandeFournisseurRequestDTO dto, CommandeFournisseur commande) {
        List<CommandeFournisseurMatiere> matieres =
                Optional.ofNullable(dto.getCommandeFournisseurMatieres())
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(commandeFournisseurMatiereMapper::toEntity)
                        .peek(m -> m.setCommandeFournisseur(commande))
                        .collect(Collectors.toList());

        commande.setCommandeFournisseurMatieres(matieres);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!commandeFournisseurRepository.existsById(id)) {
            throw new ResourceNotFoundException("Commande not found with id: " + id);
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

    @Override
    @Transactional
    public CommandeFournisseurResponseDTO startProductionOrder(Long id) {
        CommandeFournisseur commandeFournisseur = commandeFournisseurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande not found with id: " + id));
        commandeFournisseur.setStatus(FournisseurOrderStatus.EN_COURS);
        return commandeFournisseurMapper.toResponseDTO(commandeFournisseurRepository.save(commandeFournisseur));
    }

    @Override
    @Transactional
    public CommandeFournisseurResponseDTO completeProductionOrder(Long id) {
        CommandeFournisseur commandeFournisseur = commandeFournisseurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande not found with id: " + id));

        commandeFournisseur.setStatus(FournisseurOrderStatus.RECUE);

        if (commandeFournisseur.getCommandeFournisseurMatieres() != null) {
            commandeFournisseur.getCommandeFournisseurMatieres().forEach(commandeMatiere -> {
                MatierePremiere matiere = commandeMatiere.getMatierePremiere();
                if (matiere != null) {
                    int nouvelleQuantite = matiere.getStockQuantity() - commandeMatiere.getQuantite();
                    if (nouvelleQuantite < 0) {
                        throw new IllegalStateException(
                                "Stock insuffisant pour la matière " + matiere.getMatierePremiereId() +
                                        ". Stock actuel: " + matiere.getStockQuantity() +
                                        ", quantité demandée: " + commandeMatiere.getQuantite()
                        );
                    }
                    matiere.setStockQuantity(nouvelleQuantite);
                }
            });
        }

        return commandeFournisseurMapper.toResponseDTO(commandeFournisseurRepository.save(commandeFournisseur));
    }


    @Override
    @Transactional
    public CommandeFournisseurResponseDTO cancelProductionOrder(Long id) {
        CommandeFournisseur commandeFournisseur = commandeFournisseurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande not found with id: " + id));
        commandeFournisseur.setStatus(FournisseurOrderStatus.ANNULEE);
        return commandeFournisseurMapper.toResponseDTO(commandeFournisseurRepository.save(commandeFournisseur));
    }

}
