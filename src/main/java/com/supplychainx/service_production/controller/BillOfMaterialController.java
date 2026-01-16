package com.supplychainx.service_production.controller;

import com.supplychainx.handler.GlobalSuccessHandler;
import com.supplychainx.service_production.dto.Request.BillOfMaterialRequestDTO;
import com.supplychainx.service_production.dto.Response.BillOfMaterialResponseDTO;
import com.supplychainx.service_production.service.BillOfMaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bill-of-material")
public class BillOfMaterialController {
    private final BillOfMaterialService billOfMaterialService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF_PRODUCTION', 'PLANIFICATEUR', 'SUPERVISEUR_PRODUCTION')")
    public BillOfMaterialResponseDTO createBillOfMaterial(@Valid @RequestBody BillOfMaterialRequestDTO billOfMaterialRequestDTO) {
        return ResponseEntity.status(201).body(billOfMaterialService.createBillOfMaterial(billOfMaterialRequestDTO)).getBody();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF_PRODUCTION', 'PLANIFICATEUR', 'SUPERVISEUR_PRODUCTION')")
    @GetMapping("/{bomId}")
    public BillOfMaterialResponseDTO getBillOfMaterialById(@PathVariable("bomId") Long bomId) {
        return billOfMaterialService.getBillOfMaterialById(bomId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF_PRODUCTION', 'PLANIFICATEUR', 'SUPERVISEUR_PRODUCTION')")
    @PutMapping("/{bomId}")
    public BillOfMaterialResponseDTO updateBillOfMaterial(@PathVariable("bomId") Long bomId,
                                                          @Valid @RequestBody BillOfMaterialRequestDTO billOfMaterialRequestDTO) {
        return billOfMaterialService.updateBillOfMaterial(bomId, billOfMaterialRequestDTO);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF_PRODUCTION', 'PLANIFICATEUR', 'SUPERVISEUR_PRODUCTION')")
    @DeleteMapping("/{bomId}")
    public ResponseEntity<Map<String, Object>> deleteBillOfMaterial(@PathVariable("bomId") Long bomId) {
        billOfMaterialService.deleteBillOfMaterial(bomId);
        return GlobalSuccessHandler.handleDeleted("Bill of Material deleted successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF_PRODUCTION', 'PLANIFICATEUR', 'SUPERVISEUR_PRODUCTION')")
    @GetMapping
    public Object getAllBillOfMaterials(int page, int size) {
        return billOfMaterialService.getAllBillOfMaterials(page, size);
    }

}