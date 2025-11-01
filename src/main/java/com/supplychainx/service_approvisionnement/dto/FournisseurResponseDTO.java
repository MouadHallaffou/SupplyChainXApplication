package com.supplychainx.service_approvisionnement.dto;

import lombok.Builder;

@Builder
public record FournisseurResponseDTO(String name, String contactEmail, String phoneNumber, String address,
                                     Double rating, Integer leadTimeDays, Boolean isActive) {
}
