package com.supplychainx.service_livraison.mapper;

import com.supplychainx.exception.ResourceNotFoundException;
import com.supplychainx.service_livraison.dto.Request.LivraisonRequestDTO;
import com.supplychainx.service_livraison.dto.Response.LivraisonResponseDTO;
import com.supplychainx.service_livraison.model.Address;
import com.supplychainx.service_livraison.model.ClientOrder;
import com.supplychainx.service_livraison.model.Livraison;
import com.supplychainx.service_livraison.model.enums.LivraisonStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LivraisonMapper {

    public Livraison toEntity(LivraisonRequestDTO dto) {
        try {
            Livraison livraison = new Livraison();
            ClientOrder clientOrder = new ClientOrder();
            clientOrder.setClientOrderId(dto.getClientOrderId());
            livraison.setClientOrder(clientOrder);
            livraison.setVehicule(dto.getVehicule());
            livraison.setDriverName(dto.getDriverName());
            livraison.setStatus(LivraisonStatus.valueOf(dto.getStatus()));
            livraison.setCost(dto.getCost());
            livraison.setDeliveryDate(dto.getDeliveryDate());
            return livraison;
        } catch (Exception e) {
            throw new ResourceNotFoundException("Error creating livraison: " + e.getMessage());
        }
    }

    public LivraisonResponseDTO toResponseDTO(Livraison livraison) {
        try {
            if (livraison == null) {
                return null;
            }

            // Gestion de l'adresse
            String address = "Adresse non disponible";
            if (livraison.getClientOrder() != null &&
                    livraison.getClientOrder().getClient() != null &&
                    livraison.getClientOrder().getClient().getAddresses() != null &&
                    !livraison.getClientOrder().getClient().getAddresses().isEmpty()) {

                Address addr = livraison.getClientOrder().getClient().getAddresses().get(0);
                address = String.format("%s, %s, %s, %s",
                        addr.getStreet() != null ? addr.getStreet() : "",
                        addr.getCity() != null ? addr.getCity() : "",
                        addr.getState() != null ? addr.getState() : "",
                        addr.getZipCode() != null ? addr.getZipCode() : "");
            }

            return new LivraisonResponseDTO(
                    livraison.getLivraisonId(),
                    livraison.getClientOrder() != null ? livraison.getClientOrder().getClientOrderId() : null,
                    livraison.getVehicule(),
                    livraison.getDriverName(),
                    livraison.getStatus() != null ? livraison.getStatus().name() : null,
                    address,
                    livraison.getCost(),
                    livraison.getDeliveryDate(),
                    livraison.getCreatedAt(),
                    livraison.getUpdatedAt()
            );
        } catch (Exception e) {
            log.error("Error mapping Livraison to ResponseDTO: {}", e.getMessage(), e);
            throw new RuntimeException("Error mapping livraison: " + e.getMessage());
        }
    }

    public void toUpdateResponseDTO(LivraisonRequestDTO dto, Livraison livraison) {
        new LivraisonResponseDTO(
                livraison.getLivraisonId(),
                dto.getClientOrderId(),
                dto.getVehicule(),
                dto.getDriverName(),
                dto.getStatus(),
                null,
                dto.getCost(),
                dto.getDeliveryDate(),
                livraison.getCreatedAt(),
                null
        );
    }
}