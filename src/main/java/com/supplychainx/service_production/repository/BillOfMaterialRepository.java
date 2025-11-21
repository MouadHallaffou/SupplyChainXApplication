package com.supplychainx.service_production.repository;

import com.supplychainx.service_production.model.BillOfMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillOfMaterialRepository extends JpaRepository<BillOfMaterial, Long> {
    List<BillOfMaterial> findBillOfMaterialsByProduct_ProductId(Long productProductId);
}
