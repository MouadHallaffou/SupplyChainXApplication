package com.supplychainx.service_livraison.service;

import com.supplychainx.service_livraison.dto.Request.AddressRequestDTO;
import com.supplychainx.service_livraison.dto.Response.AddressResponseDTO;
import org.springframework.data.domain.Page;

public interface AddressService {
    AddressResponseDTO getAddressById(Long addressId);
    AddressResponseDTO createAddress(AddressRequestDTO addressRequestDTO);
    AddressResponseDTO updateAddress(Long addressId, AddressRequestDTO addressRequestDTO);
    void deleteAddress(Long addressId);
    Page<AddressResponseDTO> getAllAddresses(int page, int size, String sortBy, String direction);
}
