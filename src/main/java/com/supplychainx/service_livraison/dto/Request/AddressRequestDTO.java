package com.supplychainx.service_livraison.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddressRequestDTO {
    @NotBlank(message = "Street is required")
    private String street;
    @NotBlank(message = "City is required")
    private String city;
    private String state;
    private String country;
    @NotBlank(message = "Zip code is required")
    private String zipCode;
    @NotNull(message = "Client ID is required")
    private Long clientId;
}
