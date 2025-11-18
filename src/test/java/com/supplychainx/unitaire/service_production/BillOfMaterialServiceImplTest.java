package com.supplychainx.unitaire.service_production;

import com.supplychainx.service_approvisionnement.model.MatierePremiere;
import com.supplychainx.service_approvisionnement.repository.MatierePremiereRepository;
import com.supplychainx.service_production.dto.Request.BillOfMaterialRequestDTO;
import com.supplychainx.service_production.dto.Response.BillOfMaterialResponseDTO;
import com.supplychainx.service_production.mapper.BillOfMaterialMapper;
import com.supplychainx.service_production.model.BillOfMaterial;
import com.supplychainx.service_production.model.Product;
import com.supplychainx.service_production.repository.BillOfMaterialRepository;
import com.supplychainx.service_production.repository.ProductRepository;
import com.supplychainx.service_production.service.BillOfMaterialServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillOfMaterialServiceImplTest {

    @Mock
    private BillOfMaterialRepository billOfMaterialRepository;
    @Mock
    private BillOfMaterialMapper billOfMaterialMapper;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private MatierePremiereRepository matierePremiereRepository;

    @InjectMocks
    private BillOfMaterialServiceImpl billOfMaterialService;

    @Test
    void createBillOfMaterial_ShouldReturnResponseDTO() {
        BillOfMaterialRequestDTO request = new BillOfMaterialRequestDTO();
        BillOfMaterial bom = new BillOfMaterial();
        BillOfMaterialResponseDTO response = new BillOfMaterialResponseDTO(1L, null, null, null, null, null);

        when(billOfMaterialMapper.toEntity(request)).thenReturn(bom);
        when(billOfMaterialRepository.save(bom)).thenReturn(bom);
        when(billOfMaterialMapper.toResponseDto(bom)).thenReturn(response);

        BillOfMaterialResponseDTO result = billOfMaterialService.createBillOfMaterial(request);

        assertEquals(response, result);
    }

    @Test
    void getBillOfMaterialById_ShouldReturnResponseDTO_WhenExists() {
        BillOfMaterial bom = new BillOfMaterial();
        BillOfMaterialResponseDTO response = new BillOfMaterialResponseDTO(1L, null, null, null, null, null);

        when(billOfMaterialRepository.findById(1L)).thenReturn(Optional.of(bom));
        when(billOfMaterialMapper.toResponseDto(bom)).thenReturn(response);

        BillOfMaterialResponseDTO result = billOfMaterialService.getBillOfMaterialById(1L);

        assertEquals(response, result);
    }

    @Test
    void updateBillOfMaterial_ShouldReturnUpdatedDTO_WithProductAndMatiere() {
        BillOfMaterialRequestDTO request = new BillOfMaterialRequestDTO();
        request.setBomId(1L);
        request.setProductId(1L);
        request.setMatierePremiereId(1L);
        BillOfMaterial existing = new BillOfMaterial();
        Product product = new Product();
        MatierePremiere matiere = new MatierePremiere();
        BillOfMaterialResponseDTO response = new BillOfMaterialResponseDTO(1L, null, null, null, null, null);

        when(billOfMaterialRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(matierePremiereRepository.findById(1L)).thenReturn(Optional.of(matiere));
        when(billOfMaterialRepository.save(existing)).thenReturn(existing);
        when(billOfMaterialMapper.toResponseDto(existing)).thenReturn(response);

        BillOfMaterialResponseDTO result = billOfMaterialService.updateBillOfMaterial(1L, request);

        assertEquals(response, result);
    }

    @Test
    void deleteBillOfMaterial_ShouldDelete_WhenExists() {
        BillOfMaterial bom = new BillOfMaterial();
        when(billOfMaterialRepository.findById(1L)).thenReturn(Optional.of(bom));

        billOfMaterialService.deleteBillOfMaterial(1L);

        verify(billOfMaterialRepository).delete(bom);
    }

    @Test
    void getAllBillOfMaterials_ShouldReturnPage() {
        Page<BillOfMaterial> page = new PageImpl<>(List.of(new BillOfMaterial()));
        BillOfMaterialResponseDTO response = new BillOfMaterialResponseDTO(1L, null, null, null, null, null);

        when(billOfMaterialRepository.findAll(any(PageRequest.class))).thenReturn(page);
        when(billOfMaterialMapper.toResponseDto(any())).thenReturn(response);

        Page<BillOfMaterialResponseDTO> result = billOfMaterialService.getAllBillOfMaterials(0, 10);

        assertFalse(result.getContent().isEmpty());
    }
}