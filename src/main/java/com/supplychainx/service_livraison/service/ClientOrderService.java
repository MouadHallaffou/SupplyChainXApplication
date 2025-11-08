package com.supplychainx.service_livraison.service;

import com.supplychainx.service_livraison.dto.Request.ClientOrderRequestDTO;
import com.supplychainx.service_livraison.dto.Response.ClientOrderResponseDTO;
import com.supplychainx.service_livraison.model.enums.ClientOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClientOrderService {

    ClientOrderResponseDTO createClientOrder(ClientOrderRequestDTO clientOrderRequestDTO);

    ClientOrderResponseDTO updateClientOrder(Long orderId, ClientOrderRequestDTO clientOrderRequestDTO);

    ClientOrderResponseDTO cancelClientOrder(Long orderId);

    Page<ClientOrderResponseDTO> getAllClientOrders(Pageable pageable);

    List<ClientOrderResponseDTO> getAllClientOrders();

    ClientOrderResponseDTO getClientOrderById(Long orderId);

    Page<ClientOrderResponseDTO> getClientOrdersByStatus(ClientOrderStatus status, Pageable pageable);

    void deleteClientOrder(Long orderId);

}