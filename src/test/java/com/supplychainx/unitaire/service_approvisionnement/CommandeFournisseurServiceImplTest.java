package com.supplychainx.unitaire.service_approvisionnement;

import com.supplychainx.service_approvisionnement.dto.Request.CommandeFournisseurRequestDTO;
import com.supplychainx.service_approvisionnement.dto.Response.CommandeFournisseurResponseDTO;
import com.supplychainx.service_approvisionnement.mapper.CommandeFournisseurMapper;
import com.supplychainx.service_approvisionnement.mapper.CommandeFournisseurMatiereMapper;
import com.supplychainx.service_approvisionnement.model.CommandeFournisseur;
import com.supplychainx.service_approvisionnement.model.enums.FournisseurOrderStatus;
import com.supplychainx.service_approvisionnement.repository.CommandeFournisseurRepository;
import com.supplychainx.service_approvisionnement.service.CommandeFournisseurServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommandeFournisseurServiceImplTest {

    @Mock private CommandeFournisseurMapper commandeFournisseurMapper;
    @Mock private CommandeFournisseurMatiereMapper commandeFournisseurMatiereMapper;
    @Mock private CommandeFournisseurRepository commandeFournisseurRepository;

    @InjectMocks private CommandeFournisseurServiceImpl commandeFournisseurService;

    @Test void create_ShouldReturnResponseDTO() {
        CommandeFournisseurRequestDTO dto = new CommandeFournisseurRequestDTO();
        CommandeFournisseur commande = new CommandeFournisseur();
        CommandeFournisseurResponseDTO responseDTO = new CommandeFournisseurResponseDTO(1L, LocalDate.now(), FournisseurOrderStatus.RECUE, "Mouad hlf", List.of());

        when(commandeFournisseurMapper.toEntity(dto)).thenReturn(commande);
        when(commandeFournisseurRepository.save(commande)).thenReturn(commande);
        when(commandeFournisseurMapper.toResponseDTO(commande)).thenReturn(responseDTO);

        CommandeFournisseurResponseDTO result = commandeFournisseurService.create(dto);

        assertEquals(responseDTO, result);
    }

    @Test void getById_ShouldReturnResponseDTO_WhenExists() {
        CommandeFournisseur commande = new CommandeFournisseur();
        CommandeFournisseurResponseDTO responseDTO = new CommandeFournisseurResponseDTO(1L, null, null, null, List.of());

        when(commandeFournisseurRepository.findById(1L)).thenReturn(Optional.of(commande));
        when(commandeFournisseurMapper.toResponseDTO(commande)).thenReturn(responseDTO);

        CommandeFournisseurResponseDTO result = commandeFournisseurService.getById(1L);

        assertEquals(responseDTO, result);
    }

    @Test void update_ShouldReturnUpdatedDTO() {
        CommandeFournisseurRequestDTO dto = new CommandeFournisseurRequestDTO();
        CommandeFournisseur existing = new CommandeFournisseur();
        CommandeFournisseurResponseDTO responseDTO = new CommandeFournisseurResponseDTO(1L, null, null, null, List.of());

        when(commandeFournisseurRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(commandeFournisseurRepository.save(existing)).thenReturn(existing);
        when(commandeFournisseurMapper.toResponseDTO(existing)).thenReturn(responseDTO);

        CommandeFournisseurResponseDTO result = commandeFournisseurService.update(dto, 1L);

        assertEquals(responseDTO, result);
    }

    @Test void delete_ShouldDelete_WhenExists() {
        when(commandeFournisseurRepository.existsById(1L)).thenReturn(true);

        commandeFournisseurService.delete(1L);

        verify(commandeFournisseurRepository).deleteById(1L);
    }

    @Test void getAllCommandeFournisseurs_ShouldReturnPage() {
        Pageable pageable = Pageable.ofSize(10).withPage(0);
        Page<CommandeFournisseur> page = new PageImpl<>(List.of(new CommandeFournisseur()));

        when(commandeFournisseurRepository.findAll(pageable)).thenReturn(page);
        when(commandeFournisseurMapper.toResponseDTO(any())).thenReturn(new CommandeFournisseurResponseDTO(1L, null, null, null, List.of()));

        Page<CommandeFournisseurResponseDTO> result = commandeFournisseurService.getAllCommandeFournisseurs(0, 10);

        assertFalse(result.getContent().isEmpty());
    }

    @Test void startProductionOrder_ShouldUpdateStatus() {
        CommandeFournisseur commande = new CommandeFournisseur();
        CommandeFournisseurResponseDTO responseDTO = new CommandeFournisseurResponseDTO(1L, null, FournisseurOrderStatus.EN_COURS, null, List.of());

        when(commandeFournisseurRepository.findById(1L)).thenReturn(Optional.of(commande));
        when(commandeFournisseurRepository.save(commande)).thenReturn(commande);
        when(commandeFournisseurMapper.toResponseDTO(commande)).thenReturn(responseDTO);

        CommandeFournisseurResponseDTO result = commandeFournisseurService.startProductionOrder(1L);

        assertEquals(FournisseurOrderStatus.EN_COURS, result.status());
    }
}