package com.supplychainx.unitaire.service_approvisionnement;

import com.supplychainx.service_approvisionnement.dto.Request.FournisseurRequestDTO;
import com.supplychainx.service_approvisionnement.dto.Response.FournisseurResponseDTO;
import com.supplychainx.service_approvisionnement.mapper.FournisseurMapper;
import com.supplychainx.service_approvisionnement.model.Fournisseur;
import com.supplychainx.service_approvisionnement.repository.CommandeFournisseurRepository;
import com.supplychainx.service_approvisionnement.repository.FournisseurRepository;
import com.supplychainx.service_approvisionnement.service.FournisseurServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FournisseurServiceImplTest {

    @Mock private FournisseurRepository fournisseurRepository;
    @Mock private FournisseurMapper fournisseurMapper;
    @Mock private CommandeFournisseurRepository commandeFournisseurRepository;

    @InjectMocks private FournisseurServiceImpl fournisseurService;

    @Test void create_ShouldReturnResponseDTO() {
        FournisseurRequestDTO dto = new FournisseurRequestDTO();
        Fournisseur fournisseur = new Fournisseur();
        FournisseurResponseDTO responseDTO = FournisseurResponseDTO.builder().fournisseurId(1L).build();

        when(fournisseurMapper.toEntity(dto)).thenReturn(fournisseur);
        when(fournisseurRepository.save(fournisseur)).thenReturn(fournisseur);
        when(fournisseurMapper.toResponseDTO(fournisseur)).thenReturn(responseDTO);

        FournisseurResponseDTO result = fournisseurService.create(dto);

        assertEquals(responseDTO, result);
    }

    @Test void getById_ShouldReturnResponseDTO_WhenExists() {
        Fournisseur fournisseur = new Fournisseur();
        FournisseurResponseDTO responseDTO = FournisseurResponseDTO.builder().fournisseurId(1L).build();

        when(fournisseurRepository.findById(1L)).thenReturn(Optional.of(fournisseur));
        when(fournisseurMapper.toResponseDTO(fournisseur)).thenReturn(responseDTO);

        FournisseurResponseDTO result = fournisseurService.getById(1L);

        assertEquals(responseDTO, result);
    }

    @Test void update_ShouldReturnUpdatedDTO() {
        FournisseurRequestDTO dto = new FournisseurRequestDTO();
        Fournisseur existing = new Fournisseur();
        FournisseurResponseDTO responseDTO = FournisseurResponseDTO.builder().fournisseurId(1L).build();

        when(fournisseurRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(fournisseurRepository.save(existing)).thenReturn(existing);
        when(fournisseurMapper.toResponseDTO(existing)).thenReturn(responseDTO);

        FournisseurResponseDTO result = fournisseurService.update(dto, 1L);

        assertEquals(responseDTO, result);
    }

    @Test void delete_ShouldDelete_WhenNoActiveOrders() {
        when(fournisseurRepository.existsById(1L)).thenReturn(true);
        when(commandeFournisseurRepository.findAll()).thenReturn(List.of());

        fournisseurService.delete(1L);

        verify(fournisseurRepository).deleteById(1L);
    }

    @Test void getAll_ShouldReturnList() {
        when(fournisseurRepository.findAll()).thenReturn(List.of(new Fournisseur()));
        when(fournisseurMapper.toResponseDTO(any())).thenReturn(FournisseurResponseDTO.builder().build());

        List<FournisseurResponseDTO> result = fournisseurService.getAll();

        assertFalse(result.isEmpty());
    }
}