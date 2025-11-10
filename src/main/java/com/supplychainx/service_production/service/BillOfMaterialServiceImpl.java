package com.supplychainx.service_production.service;

import com.supplychainx.exception.ResourceNotFoundException;
import com.supplychainx.service_approvisionnement.model.MatierePremiere;
import com.supplychainx.service_approvisionnement.repository.MatierePremiereRepository;
import com.supplychainx.service_production.dto.Request.BillOfMaterialRequestDTO;
import com.supplychainx.service_production.dto.Response.BillOfMaterialResponseDTO;
import com.supplychainx.service_production.mapper.BillOfMaterialMapper;
import com.supplychainx.service_production.model.BillOfMaterial;
import com.supplychainx.service_production.model.Product;
import com.supplychainx.service_production.repository.BillOfMaterialRepository;
import com.supplychainx.service_production.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BillOfMaterialServiceImpl implements BillOfMaterialService {
    private final BillOfMaterialMapper billOfMaterialMapper;
    private final BillOfMaterialRepository billOfMaterialRepository;
    private final ProductRepository productRepository;
    private final MatierePremiereRepository matierePremiereRepository;

    @Override
    @Transactional
    public BillOfMaterialResponseDTO createBillOfMaterial(BillOfMaterialRequestDTO billOfMaterialRequestDTO) {
        BillOfMaterial billOfMaterial = billOfMaterialMapper.toEntity(billOfMaterialRequestDTO);
        BillOfMaterial saveBillOfMaterial = billOfMaterialRepository.save(billOfMaterial);
        return billOfMaterialMapper.toResponseDto(saveBillOfMaterial);
    }

    @Override
    public BillOfMaterialResponseDTO getBillOfMaterialById(Long bomId) {
        BillOfMaterial billOfMaterial = billOfMaterialRepository.findById(bomId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill of Material not found with id: " + bomId));
        return billOfMaterialMapper.toResponseDto(billOfMaterial);
    }

    @Override
    @Transactional
    public BillOfMaterialResponseDTO updateBillOfMaterial(Long bomId, BillOfMaterialRequestDTO dto) {
        BillOfMaterial entity = billOfMaterialRepository.findById(bomId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill of Material not found with id: " + bomId));

        billOfMaterialMapper.updateEntityFromDto(dto, entity);

        if (dto.getProductId() != null) {
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            entity.setProduct(product);
        }

        if (dto.getMatierePremiereId() != null) {
            MatierePremiere matierePremiere = matierePremiereRepository.findById(dto.getMatierePremiereId())
                    .orElseThrow(() -> new ResourceNotFoundException("MatierePremiere not found"));
            entity.setMatierePremiere(matierePremiere);
        }
        BillOfMaterial saved = billOfMaterialRepository.save(entity);
        return billOfMaterialMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public void deleteBillOfMaterial(Long bomId) {
        BillOfMaterial existingBillOfMaterial = billOfMaterialRepository.findById(bomId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill of Material not found with id: " + bomId));
        billOfMaterialRepository.delete(existingBillOfMaterial);
    }

    @Override
    public Page<BillOfMaterialResponseDTO> getAllBillOfMaterials(int page, int size) {
        Page<BillOfMaterial> billOfMaterialPage = billOfMaterialRepository.findAll(PageRequest.of(page, size));
        return billOfMaterialPage.map(billOfMaterialMapper::toResponseDto);
    }

}
