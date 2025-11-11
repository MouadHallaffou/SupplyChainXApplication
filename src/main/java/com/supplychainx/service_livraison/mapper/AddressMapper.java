package com.supplychainx.service_livraison.mapper;

import com.supplychainx.service_livraison.dto.Request.AddressRequestDTO;
import com.supplychainx.service_livraison.dto.Response.AddressResponseDTO;
import com.supplychainx.service_livraison.model.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public Address toEntity(AddressRequestDTO addressRequestDTO) {
        if (addressRequestDTO == null) {
            return null;
        }

        Address address = new Address();
        address.setStreet(addressRequestDTO.getStreet());
        address.setCity(addressRequestDTO.getCity());
        address.setState(addressRequestDTO.getState());
        address.setCountry(addressRequestDTO.getCountry());
        address.setZipCode(addressRequestDTO.getZipCode());
        return address;
    }

    public AddressResponseDTO toResponseDTO(Address address) {
        if (address == null) {
            return null;
        }

        return new AddressResponseDTO(
                address.getAddressId(),
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getZipCode(),
                address.getCountry(),
                address.getClient() != null ? address.getClient().getClientId() : null,
                address.getClient() != null ? address.getClient().getName() : null,
                address.getCreatedAt(),
                address.getUpdatedAt()
        );
    }

    public void toUpdateEntity(AddressRequestDTO addressRequestDTO, Address address) {
        if (addressRequestDTO == null || address == null) {
            return;
        }

        address.setStreet(addressRequestDTO.getStreet());
        address.setCity(addressRequestDTO.getCity());
        address.setCountry(addressRequestDTO.getCountry());
        address.setState(addressRequestDTO.getState());
        address.setZipCode(addressRequestDTO.getZipCode());

    }
}