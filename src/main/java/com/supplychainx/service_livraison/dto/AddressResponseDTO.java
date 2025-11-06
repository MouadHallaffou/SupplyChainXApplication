package com.supplychainx.service_livraison.dto;

import java.time.LocalDateTime;

public record AddressResponseDTO(Long addressId,
                                 String street,
                                 String city,
                                 String state,
                                 String zipCode,
//                                 ClientResponseDTO client,
                                 LocalDateTime createdAt,
                                 LocalDateTime updatedAt) {
}
