package com.supplychainx.service_production.service;

import com.supplychainx.service_production.dto.Request.BillOfMaterialRequestDTO;
import com.supplychainx.service_production.dto.Response.BillOfMaterialResponseDTO;
import org.springframework.data.domain.Page;

public interface BillOfMaterialService {
    BillOfMaterialResponseDTO createBillOfMaterial(BillOfMaterialRequestDTO billOfMaterialRequestDTO);
    BillOfMaterialResponseDTO getBillOfMaterialById(Long bomId);
    BillOfMaterialResponseDTO updateBillOfMaterial(Long bomId, BillOfMaterialRequestDTO billOfMaterialRequestDTO);
    void deleteBillOfMaterial(Long bomId);
    Page<BillOfMaterialResponseDTO> getAllBillOfMaterials(int page, int size);
}
