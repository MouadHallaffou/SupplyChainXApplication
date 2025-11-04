package com.supplychainx.service_production.service;

import com.supplychainx.service_production.dto.BillOfMaterialRequestDTO;
import com.supplychainx.service_production.dto.BillOfMaterialResponseDTO;
import org.springframework.data.domain.Page;

public interface BillOfMaterialService {
    BillOfMaterialResponseDTO createBillOfMaterial(BillOfMaterialRequestDTO billOfMaterialRequestDTO);
    BillOfMaterialResponseDTO getBillOfMaterialById(Long bomId);
    BillOfMaterialResponseDTO updateBillOfMaterial(Long bomId, BillOfMaterialRequestDTO billOfMaterialRequestDTO);
    void deleteBillOfMaterial(Long bomId);
    Page<BillOfMaterialResponseDTO> getAllBillOfMaterials(int page, int size);
}
