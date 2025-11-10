package com.supplychainx.service_livraison.service;

import com.supplychainx.exception.ResourceNotFoundException;
import com.supplychainx.service_livraison.dto.Request.AddressRequestDTO;
import com.supplychainx.service_livraison.dto.Response.AddressResponseDTO;
import com.supplychainx.service_livraison.mapper.AddressMapper;
import com.supplychainx.service_livraison.model.Address;
import com.supplychainx.service_livraison.model.Client;
import com.supplychainx.service_livraison.repository.AddressRepository;
import com.supplychainx.service_livraison.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressMapper addressMapper;
    private final AddressRepository addressRepository;
    private final ClientRepository clientRepository;

    @Override
    public AddressResponseDTO getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId).orElseThrow(() ->
                new ResourceNotFoundException("Address not found with id: " + addressId));
        return addressMapper.toResponseDTO(address);
    }

    @Override
    @Transactional
    public AddressResponseDTO createAddress(AddressRequestDTO addressRequestDTO) {
        if (addressRepository.findByStreetIgnoreCase(addressRequestDTO.getStreet()) != null) {
            throw new ResourceNotFoundException("Address with street: " + addressRequestDTO.getStreet() + " already exists.");
        }
        Client client = clientRepository.findById(addressRequestDTO.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        Address address = addressMapper.toEntity(addressRequestDTO);
        address.setClient(client);
        Address savedAddress = addressRepository.save(address);
        return addressMapper.toResponseDTO(savedAddress);
    }

    @Override
    @Transactional
    public AddressResponseDTO updateAddress(Long addressId, AddressRequestDTO addressRequestDTO) {
        Address existingAddress = addressRepository.findById(addressId).orElseThrow(() ->
                new ResourceNotFoundException("Address not found with id: " + addressId));
        if (!existingAddress.getStreet().equalsIgnoreCase(addressRequestDTO.getStreet()) &&
                addressRepository.findByStreetIgnoreCase(addressRequestDTO.getStreet()) != null) {
            throw new ResourceNotFoundException("Address with street: " + addressRequestDTO.getStreet() + " already exists.");
        }
        if (addressRequestDTO.getClientId() != null) {
            Client client = clientRepository.findById(addressRequestDTO.getClientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
            existingAddress.setClient(client);
        }
        addressMapper.toUpdateEntity(addressRequestDTO, existingAddress);
        Address updatedAddress = addressRepository.save(existingAddress);
        return addressMapper.toResponseDTO(updatedAddress);
    }

    @Override
    @Transactional
    public void deleteAddress(Long addressId) {
        Address existingAddress = addressRepository.findById(addressId).orElseThrow(() ->
                new ResourceNotFoundException("Address not found with id: " + addressId));
        addressRepository.delete(existingAddress);
    }

    @Override
    public Page<AddressResponseDTO> getAllAddresses(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Page<Address> addressPage = addressRepository.findAll(PageRequest.of(page, size, sort));
        return addressPage.map(addressMapper::toResponseDTO);
    }

}
