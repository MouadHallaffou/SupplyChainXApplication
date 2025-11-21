package com.supplychainx.service_production.service;

import com.supplychainx.exception.ResourceNotFoundException;
import com.supplychainx.service_production.dto.Request.ProductOrderRequestDTO;
import com.supplychainx.service_production.dto.Response.ProductOrderResponseDTO;
import com.supplychainx.service_production.mapper.ProductOrderMapper;
import com.supplychainx.service_production.model.BillOfMaterial;
import com.supplychainx.service_production.model.Product;
import com.supplychainx.service_production.model.ProductOrder;
import com.supplychainx.service_production.model.enums.ProductionOrderStatus;
import com.supplychainx.service_production.repository.BillOfMaterialRepository;
import com.supplychainx.service_production.repository.ProductOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductOrderServiceImpl implements ProductOrderService {
    private final ProductOrderMapper productOrderMapper;
    private final ProductOrderRepository productOrderRepository;
    private final BillOfMaterialRepository billOfMaterialRepository;
    private final BillOfMaterialService billOfMaterialService;

    @Override
    public ProductOrderResponseDTO createProductOrder(ProductOrderRequestDTO productOrderRequestDTO) {
        ProductOrder productOrder = productOrderMapper.toEntity(productOrderRequestDTO);
        productOrder.setStatus(ProductionOrderStatus.EN_ATTENTE);
        ProductOrder savedProductOrder = productOrderRepository.save(productOrder);
        return productOrderMapper.toResponseDTO(savedProductOrder);
    }

    @Override
    public ProductOrderResponseDTO getProductOrderById(Long productOrderId) {
        ProductOrder productOrder = productOrderRepository.findById(productOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Product Order not found with id: " + productOrderId));
        return productOrderMapper.toResponseDTO(productOrder);
    }

    @Override
    public ProductOrderResponseDTO updateProductOrder(Long productOrderId, ProductOrderRequestDTO productOrderRequestDTO) {
        ProductOrder existingProductOrder = productOrderRepository.findById(productOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Product Order not found with id: " + productOrderId));
        productOrderMapper.updateEntityFromDto(productOrderRequestDTO, existingProductOrder);
        existingProductOrder.setProductOrderId(productOrderId);
        ProductOrder updatedProductOrder = productOrderRepository.save(existingProductOrder);
        return productOrderMapper.toResponseDTO(updatedProductOrder);
    }

    @Override
    @Transactional
    public void deleteProductOrder(Long productOrderId) {
        getProductOrderById(productOrderId);
        productOrderRepository.deleteById(productOrderId);
    }

    @Override
    public Page<ProductOrderResponseDTO> getAllProductOrders(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new ResourceNotFoundException("Invalid page or size parameters");
        }
        return productOrderRepository.findAll()
                .stream()
                .map(productOrderMapper::toResponseDTO)
                .collect(Collectors.collectingAndThen(Collectors.toList(),
                        PageImpl::new));
    }

    @Override
    @Transactional
    public ProductOrderResponseDTO startProduction(Long productOrderId) {
        ProductOrder productOrder = productOrderRepository.findById(productOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Product Order not found with id: " + productOrderId));
        List<BillOfMaterial> billOfMaterials = billOfMaterialRepository.findBillOfMaterialsByProduct_ProductId(productOrder.getProduct().getProductId());
        if (billOfMaterials == null) {
            billOfMaterials = List.of();
        }
        for (BillOfMaterial bom : billOfMaterials) {
            if (bom.getMatierePremiere() == null) {
                throw new ResourceNotFoundException("Raw material missing for BOM id: " + bom.getBomId());
            }
            int required = bom.getQuantity() * productOrder.getQuantity();
            int available = bom.getMatierePremiere().getStockQuantity();
            if (available < required) {
                throw new ResourceNotFoundException("Insufficient stock for raw material id: " + bom.getMatierePremiere().getMatierePremiereId());
            }
            bom.getMatierePremiere().setStockQuantity(available - required);
            billOfMaterialRepository.save(bom);
        }

        productOrder.setStatus(ProductionOrderStatus.EN_PRODUCTION);
        ProductOrder updatedProductOrder = productOrderRepository.save(productOrder);
        return productOrderMapper.toResponseDTO(updatedProductOrder);
    }

    @Override
    @Transactional
    public ProductOrderResponseDTO completeProduction(Long productOrderId) {
        ProductOrder productOrder = productOrderRepository.findById(productOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Product Order not found with id: " + productOrderId));
        productOrder.setStatus(ProductionOrderStatus.TERMINE);
        ProductOrder updatedProductOrder = productOrderRepository.save(productOrder);
        return productOrderMapper.toResponseDTO(updatedProductOrder);
    }

    @Override
    @Transactional
    public ProductOrderResponseDTO cancelProduction(Long productOrderId) {
        ProductOrder productOrder = productOrderRepository.findById(productOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Product Order not found with id: " + productOrderId));
        productOrder.setStatus(ProductionOrderStatus.BLOQUEE);
        ProductOrder updatedProductOrder = productOrderRepository.save(productOrder);
        return productOrderMapper.toResponseDTO(updatedProductOrder);
    }

}
