package com.supplychainx.service_production.controller;

import com.supplychainx.handler.GlobalSuccessHandler;
import com.supplychainx.service_production.dto.BillOfMaterialRequestDTO;
import com.supplychainx.service_production.dto.BillOfMaterialResponseDTO;
import com.supplychainx.service_production.service.BillOfMaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bill-of-material")
public class BillOfMaterialController {
    private final BillOfMaterialService billOfMaterialService;

    @PostMapping
    public BillOfMaterialResponseDTO createBillOfMaterial(@Valid @RequestBody BillOfMaterialRequestDTO billOfMaterialRequestDTO){
        return billOfMaterialService.createBillOfMaterial(billOfMaterialRequestDTO);
    }

    @GetMapping("/{bomId}")
    public BillOfMaterialResponseDTO getBillOfMaterialById(@PathVariable("bomId") Long bomId){
        return billOfMaterialService.getBillOfMaterialById(bomId);
    }

    @PutMapping("/{bomId}")
    public BillOfMaterialResponseDTO updateBillOfMaterial(@PathVariable("bomId") Long bomId,
                                                          @Valid @RequestBody BillOfMaterialRequestDTO billOfMaterialRequestDTO){
        return billOfMaterialService.updateBillOfMaterial(bomId, billOfMaterialRequestDTO);
    }

    @DeleteMapping("/{bomId}")
    public ResponseEntity<Map<String, Object>>  deleteBillOfMaterial(@PathVariable("bomId") Long bomId) {
        billOfMaterialService.deleteBillOfMaterial(bomId);
        return GlobalSuccessHandler.handleDeleted("Bill of Material deleted successfully");
    }

    @GetMapping
    public Object getAllBillOfMaterials(int page, int size){
        return billOfMaterialService.getAllBillOfMaterials(page, size);
    }

}
