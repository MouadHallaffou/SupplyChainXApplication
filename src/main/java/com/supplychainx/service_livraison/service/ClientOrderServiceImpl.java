package com.supplychainx.service_livraison.service;

import com.supplychainx.exception.BusinessException;
import com.supplychainx.exception.ResourceNotFoundException;
import com.supplychainx.service_livraison.dto.Request.ClientOrderRequestDTO;
import com.supplychainx.service_livraison.dto.Request.OrderProductClientRequestDTO;
import com.supplychainx.service_livraison.dto.Response.ClientOrderResponseDTO;
import com.supplychainx.service_livraison.mapper.ClientOrderMapper;
import com.supplychainx.service_livraison.model.Address;
import com.supplychainx.service_livraison.model.Client;
import com.supplychainx.service_livraison.model.ClientOrder;
import com.supplychainx.service_livraison.model.OrderProductClient;
import com.supplychainx.service_livraison.model.enums.ClientOrderStatus;
import com.supplychainx.service_livraison.repository.AddressRepository;
import com.supplychainx.service_livraison.repository.ClientOrderRepository;
import com.supplychainx.service_livraison.repository.ClientRepository;
import com.supplychainx.service_production.model.Product;
import com.supplychainx.service_production.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientOrderServiceImpl implements ClientOrderService {

    private final ClientOrderRepository clientOrderRepository;
    private final ClientOrderMapper clientOrderMapper;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;

    @Override
    @Transactional(readOnly = true)
    public ClientOrderResponseDTO getClientOrderById(Long orderId) {
        ClientOrder order = clientOrderRepository.findByIdWithOrderProducts(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Client order not found with id: " + orderId));

        return clientOrderMapper.toResponseDTO(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientOrderResponseDTO> getAllClientOrders(Pageable pageable) {
        return clientOrderRepository.findAllWithOrderProducts(pageable)
                .map(clientOrderMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientOrderResponseDTO> getClientOrdersByStatus(ClientOrderStatus status, Pageable pageable) {
        return clientOrderRepository.findByStatusWithOrderProducts(status, pageable)
                .map(clientOrderMapper::toResponseDTO);
    }

    @Override
    @Transactional
    public ClientOrderResponseDTO createClientOrder(ClientOrderRequestDTO clientOrderRequestDTO) {
        validateClientOrderRequest(clientOrderRequestDTO);

        Client client = clientRepository.findById(clientOrderRequestDTO.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + clientOrderRequestDTO.getClientId()));

        ClientOrder clientOrder = new ClientOrder();
        clientOrder.setClient(client);
        clientOrder.setStatus(ClientOrderStatus.EN_PREPARATION);
        clientOrder.setOrderNumber(generateOrderNumber());
        clientOrder.setOrderProducts(new ArrayList<>());

        Address deliveryAddress = determineDeliveryAddress(clientOrderRequestDTO, client);
        clientOrder.setDeliveryAddress(deliveryAddress);

        double totalAmount = processOrderProducts(clientOrderRequestDTO, clientOrder);
        clientOrder.setTotalAmount(totalAmount);

        ClientOrder savedOrder = clientOrderRepository.save(clientOrder);

        return clientOrderMapper.toResponseDTO(savedOrder);
    }

    @Override
    @Transactional
    public ClientOrderResponseDTO updateClientOrder(Long orderId, ClientOrderRequestDTO clientOrderRequestDTO) {
        ClientOrder existingOrder = clientOrderRepository.findByIdWithOrderProducts(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Client order not found with id: " + orderId));

        if (existingOrder.getStatus() != ClientOrderStatus.EN_PREPARATION) {
            throw new BusinessException(
                    "Cannot update order with status: " + existingOrder.getStatus() + ". Only orders in PREPARATION can be updated.",
                    "ORDER_UPDATE_NOT_ALLOWED"
            );
        }

        validateClientOrderRequest(clientOrderRequestDTO);

        Client client = clientRepository.findById(clientOrderRequestDTO.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + clientOrderRequestDTO.getClientId()));

        existingOrder.setClient(client);
        existingOrder.getOrderProducts().clear();

        double totalAmount = processOrderProducts(clientOrderRequestDTO, existingOrder);
        existingOrder.setTotalAmount(totalAmount);

        ClientOrder updatedOrder = clientOrderRepository.save(existingOrder);

        return clientOrderMapper.toResponseDTO(updatedOrder);
    }

    @Override
    @Transactional
    public ClientOrderResponseDTO cancelClientOrder(Long orderId) {
        ClientOrder order = clientOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Client order not found with id: " + orderId));

        if (order.getStatus() != ClientOrderStatus.EN_PREPARATION) {
            throw new BusinessException(
                    "Cannot cancel order with status: " + order.getStatus() + ". Only orders in PREPARATION can be cancelled.",
                    "ORDER_CANCELLATION_NOT_ALLOWED"
            );
        }

        order.setStatus(ClientOrderStatus.ANNULEE);
        ClientOrder cancelledOrder = clientOrderRepository.save(order);

        return clientOrderMapper.toResponseDTO(cancelledOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientOrderResponseDTO> getAllClientOrders() {
        return clientOrderRepository.findAll()
                .stream()
                .map(clientOrderMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteClientOrder(Long orderId) {
        ClientOrder order = clientOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Client order not found with id: " + orderId));

        if (order.getStatus() != ClientOrderStatus.ANNULEE) {
            throw new BusinessException(
                    "Cannot delete order with status: " + order.getStatus() + ". Only CANCELLED orders can be deleted.",
                    "ORDER_DELETION_NOT_ALLOWED"
            );
        }

        clientOrderRepository.delete(order);
    }


    private void validateClientOrderRequest(ClientOrderRequestDTO request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BusinessException("Order must contain at least one product", "EMPTY_ORDER_ITEMS");
        }

        for (OrderProductClientRequestDTO item : request.getItems()) {
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new BusinessException("Quantity must be greater than 0 for all products", "INVALID_QUANTITY");
            }

            if (item.getProductId() == null) {
                throw new BusinessException("Product ID is required for all items", "MISSING_PRODUCT_ID");
            }
        }

        if (request.getClientId() == null) {
            throw new BusinessException("Client ID is required", "MISSING_CLIENT_ID");
        }
    }

    private double processOrderProducts(ClientOrderRequestDTO request, ClientOrder clientOrder) {
        double totalAmount = 0.0;

        for (OrderProductClientRequestDTO item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + item.getProductId()));

            if (product.getStock() < item.getQuantity()) {
                throw new BusinessException(
                        String.format("Insufficient stock for product %s. Available: %d, Requested: %d",
                                product.getName(), product.getStock(), item.getQuantity()),
                        "INSUFFICIENT_STOCK"
                );
            }

            OrderProductClient orderProduct = new OrderProductClient();
            orderProduct.setProduct(product);
            orderProduct.setQuantity(item.getQuantity());
            orderProduct.setClientOrder(clientOrder);

            clientOrder.getOrderProducts().add(orderProduct);

            Double unitPrice = product.getCostPerUnit() != null ? product.getCostPerUnit() : 0.0;
            totalAmount += unitPrice * item.getQuantity();
        }

        return totalAmount;
    }

    private String generateOrderNumber() {
        return "CO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private Address determineDeliveryAddress(ClientOrderRequestDTO request, Client client) {
        // Si une adresse est spécifiée dans la requête
        if (request.getDeliveryAddressId() != null) {
            Address address = addressRepository.findById(request.getDeliveryAddressId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Address not found with id: " + request.getDeliveryAddressId()));

            // Vérifier que l'adresse appartient au client
            if (!address.getClient().getClientId().equals(client.getClientId())) {
                throw new BusinessException(
                        "Address does not belong to the client",
                        "INVALID_ADDRESS"
                );
            }

            return address;
        }

        // Sinon, utiliser l'adresse la plus récente du client
        Address latestAddress = client.getLatestAddress();
        if (latestAddress == null) {
            throw new BusinessException(
                    "Client has no address. Please add an address before creating an order.",
                    "NO_ADDRESS_FOUND"
            );
        }

        return latestAddress;
    }

}