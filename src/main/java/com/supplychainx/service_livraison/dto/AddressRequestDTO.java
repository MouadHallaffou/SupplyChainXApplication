package com.supplychainx.service_livraison.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressRequestDTO {
    private Long addressId;
    @NotBlank(message = "Street is required")
    private String street;
    @NotBlank(message = "City is required")
    private String city;
    private String state;
    @NotBlank(message = "Zip code is required")
    private String zipCode;
    private Long clientId;
}
