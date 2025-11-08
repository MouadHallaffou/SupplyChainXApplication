package com.supplychainx.service.service_production;

import com.supplychainx.service_production.dto.Request.ProductOrderRequestDTO;
import com.supplychainx.service_production.dto.Response.ProductOrderResponseDTO;
import com.supplychainx.service_production.mapper.ProductOrderMapper;
import com.supplychainx.service_production.model.ProductOrder;
import com.supplychainx.service_production.model.enums.ProductionOrderStatus;
import com.supplychainx.service_production.repository.ProductOrderRepository;
import com.supplychainx.service_production.service.ProductOrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductOrderServiceImplTest {

    @Mock private ProductOrderRepository productOrderRepository;
    @Mock private ProductOrderMapper productOrderMapper;
    @InjectMocks private ProductOrderServiceImpl productOrderService;

    @Test void createProductOrder_ShouldReturnResponseDTO() {
        ProductOrderRequestDTO request = new ProductOrderRequestDTO();
        ProductOrder order = new ProductOrder();
        ProductOrderResponseDTO response = new ProductOrderResponseDTO(1L, null, null, 1L, null,  null);

        when(productOrderMapper.toEntity(request)).thenReturn(order);
        when(productOrderRepository.save(order)).thenReturn(order);
        when(productOrderMapper.toResponseDTO(order)).thenReturn(response);

        ProductOrderResponseDTO result = productOrderService.createProductOrder(request);

        assertEquals(response, result);
    }

    @Test void getProductOrderById_ShouldReturnResponseDTO_WhenExists() {
        ProductOrder order = new ProductOrder();
        ProductOrderResponseDTO response = new ProductOrderResponseDTO(1L, null, null, 1L, null, null);

        when(productOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productOrderMapper.toResponseDTO(order)).thenReturn(response);

        ProductOrderResponseDTO result = productOrderService.getProductOrderById(1L);

        assertEquals(response, result);
    }

    @Test void updateProductOrder_ShouldReturnUpdatedDTO() {
        ProductOrderRequestDTO request = new ProductOrderRequestDTO();
        ProductOrder existing = new ProductOrder();
        ProductOrderResponseDTO response = new ProductOrderResponseDTO(1L, null, null, 1L, null, null);

        when(productOrderRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productOrderRepository.save(existing)).thenReturn(existing);
        when(productOrderMapper.toResponseDTO(existing)).thenReturn(response);

        ProductOrderResponseDTO result = productOrderService.updateProductOrder(1L, request);

        assertEquals(response, result);
    }

    @Test void deleteProductOrder_ShouldDelete_WhenExists() {
        ProductOrder order = new ProductOrder();
        when(productOrderRepository.findById(1L)).thenReturn(Optional.of(order));

        productOrderService.deleteProductOrder(1L);

        verify(productOrderRepository).deleteById(1L);
    }

    @Test void getAllProductOrders_ShouldReturnPage() {
        ProductOrder order = new ProductOrder();
        ProductOrderResponseDTO response = new ProductOrderResponseDTO(1L, null, null, 1L, null, null);

        when(productOrderRepository.findAll()).thenReturn(List.of(order));
        when(productOrderMapper.toResponseDTO(order)).thenReturn(response);

        Page<ProductOrderResponseDTO> result = productOrderService.getAllProductOrders(0, 10);

        assertFalse(result.getContent().isEmpty());
    }

    @Test void startProduction_ShouldUpdateStatusToEnProduction() {
        ProductOrder order = new ProductOrder();
        ProductOrderResponseDTO response = new ProductOrderResponseDTO(1L, null, ProductionOrderStatus.EN_PRODUCTION, 1L, null, null);

        when(productOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productOrderRepository.save(order)).thenReturn(order);
        when(productOrderMapper.toResponseDTO(order)).thenReturn(response);

        ProductOrderResponseDTO result = productOrderService.startProduction(1L);

        assertEquals(ProductionOrderStatus.EN_PRODUCTION, result.status());
    }

    @Test void completeProduction_ShouldUpdateStatusToTermine() {
        ProductOrder order = new ProductOrder();
        ProductOrderResponseDTO response = new ProductOrderResponseDTO(1L, null, ProductionOrderStatus.TERMINE, 1L, null, null);

        when(productOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productOrderRepository.save(order)).thenReturn(order);
        when(productOrderMapper.toResponseDTO(order)).thenReturn(response);

        ProductOrderResponseDTO result = productOrderService.completeProduction(1L);

        assertEquals(ProductionOrderStatus.TERMINE, result.status());
    }

    @Test void cancelProduction_ShouldUpdateStatusToBloquee() {
        ProductOrder order = new ProductOrder();
        ProductOrderResponseDTO response = new ProductOrderResponseDTO(1L, null, ProductionOrderStatus.BLOQUEE, 1L, null, null);

        when(productOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productOrderRepository.save(order)).thenReturn(order);
        when(productOrderMapper.toResponseDTO(order)).thenReturn(response);

        ProductOrderResponseDTO result = productOrderService.cancelProduction(1L);

        assertEquals(ProductionOrderStatus.BLOQUEE, result.status());
    }
}