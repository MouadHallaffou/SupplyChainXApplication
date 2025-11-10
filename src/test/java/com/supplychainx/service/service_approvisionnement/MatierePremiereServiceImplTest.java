package com.supplychainx.service.service_approvisionnement;

import com.supplychainx.exception.ResourceNotFoundException;
import com.supplychainx.service_approvisionnement.dto.Request.MatierePremiereRequestDTO;
import com.supplychainx.service_approvisionnement.dto.Response.MatierePremiereResponseDTO;
import com.supplychainx.service_approvisionnement.mapper.MatierePremiereMapper;
import com.supplychainx.service_approvisionnement.model.Fournisseur;
import com.supplychainx.service_approvisionnement.model.MatierePremiere;
import com.supplychainx.service_approvisionnement.repository.CommandeFournisseurRepository;
import com.supplychainx.service_approvisionnement.repository.FournisseurRepository;
import com.supplychainx.service_approvisionnement.repository.MatierePremiereRepository;
import com.supplychainx.service_approvisionnement.service.MatierePremiereServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatierePremiereServiceImplTest {

    @Mock private MatierePremiereRepository matierePremiereRepository;
    @Mock private MatierePremiereMapper matierePremiereMapper;
    @Mock private FournisseurRepository fournisseurRepository;
    @Mock private CommandeFournisseurRepository commandeFournisseurRepository;

    @InjectMocks private MatierePremiereServiceImpl matierePremiereService;

    @Test void create_ShouldReturnResponseDTO_WhenValidInput() {
        MatierePremiereRequestDTO dto = new MatierePremiereRequestDTO();
        dto.setName("Matiere1");
        dto.setMatierePremiereId(100L);
        dto.setStockQuantity(10);
        dto.setUnit("kg");
        dto.setFournisseurIds(List.of(1L));
        MatierePremiere matiere = new MatierePremiere();
        MatierePremiereResponseDTO responseDTO = new MatierePremiereResponseDTO(1L, "Matiere1", 100, 10, "kg", List.of());

        when(matierePremiereMapper.toEntity(dto)).thenReturn(matiere);
        when(fournisseurRepository.findById(1L)).thenReturn(Optional.of(new Fournisseur()));
        when(matierePremiereRepository.save(matiere)).thenReturn(matiere);
        when(matierePremiereMapper.toResponseDTO(matiere)).thenReturn(responseDTO);

        MatierePremiereResponseDTO result = matierePremiereService.create(dto);

        assertEquals(responseDTO, result);
        verify(matierePremiereRepository).save(matiere);
    }

    @Test void getById_ShouldReturnResponseDTO_WhenExists() {
        MatierePremiere matiere = new MatierePremiere();
        MatierePremiereResponseDTO responseDTO = new MatierePremiereResponseDTO(1L, "Matiere1", 100, 10, "kg", List.of());

        when(matierePremiereRepository.findById(1L)).thenReturn(Optional.of(matiere));
        when(matierePremiereMapper.toResponseDTO(matiere)).thenReturn(responseDTO);

        MatierePremiereResponseDTO result = matierePremiereService.getById(1L);

        assertEquals(responseDTO, result);
    }

    @Test void getById_ShouldThrow_WhenNotFound() {
        when(matierePremiereRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> matierePremiereService.getById(1L));
    }

    @Test void update_ShouldReturnUpdatedDTO_WhenValidInput() {
        MatierePremiereRequestDTO dto = new MatierePremiereRequestDTO();
        dto.setName("Updated");
        dto.setMatierePremiereId(150L);
        dto.setStockQuantity(15);
        dto.setUnit("kg");
        dto.setFournisseurIds(List.of(1L));
        MatierePremiere existing = new MatierePremiere();
        MatierePremiereResponseDTO responseDTO = new MatierePremiereResponseDTO(1L, "Updated", 150, 15, "kg", List.of());

        when(matierePremiereRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(fournisseurRepository.findById(1L)).thenReturn(Optional.of(new Fournisseur()));
        when(matierePremiereRepository.save(existing)).thenReturn(existing);
        when(matierePremiereMapper.toResponseDTO(existing)).thenReturn(responseDTO);

        MatierePremiereResponseDTO result = matierePremiereService.update(dto, 1L);

        assertEquals(responseDTO, result);
    }

    @Test void delete_ShouldDelete_WhenExistsAndNotUsed() {
        when(matierePremiereRepository.existsById(1L)).thenReturn(true);
        when(commandeFournisseurRepository.findAll()).thenReturn(List.of());

        matierePremiereService.delete(1L);

        verify(matierePremiereRepository).deleteById(1L);
    }

    @Test void getAll_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<MatierePremiere> page = new PageImpl<>(List.of(new MatierePremiere()));

        when(matierePremiereRepository.findAll(pageable)).thenReturn(page);
        when(matierePremiereMapper.toResponseDTO(any())).thenReturn(new MatierePremiereResponseDTO(1L, "Test", 100, 10, "kg", List.of()));

        Page<MatierePremiereResponseDTO> result = matierePremiereService.getAll(0, 10);

        assertFalse(result.getContent().isEmpty());
    }
}