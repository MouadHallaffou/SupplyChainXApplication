package com.supplychainx.service_approvisionnement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FournisseurRequestDTO {
    private Long fournisseurId;
    @NotBlank(message = "Name cannot be blank")
    private String name;
    @NotBlank(message = "Contact email cannot be blank")
    private String contactEmail;
    @NotBlank(message = "Phone number cannot be blank")
    private String phoneNumber;
    @NotBlank(message = "Address cannot be blank")
    private String address;
    @NotNull(message = "Rating cannot be null")
    private Double rating;
    @NotNull(message = "Lead time days cannot be null")
    private Integer leadTimeDays;
    @NotNull(message = "isActive cannot be null")
    private Boolean isActive;
}
