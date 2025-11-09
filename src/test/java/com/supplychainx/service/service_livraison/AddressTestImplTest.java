package com.supplychainx.service.service_livraison;

import com.supplychainx.service_livraison.dto.Request.AddressRequestDTO;
import com.supplychainx.service_livraison.dto.Response.AddressResponseDTO;
import com.supplychainx.service_livraison.mapper.AddressMapper;
import com.supplychainx.service_livraison.model.Address;
import com.supplychainx.service_livraison.model.Client;
import com.supplychainx.service_livraison.repository.AddressRepository;
import com.supplychainx.service_livraison.repository.ClientRepository;
import com.supplychainx.service_livraison.service.AddressServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressTestImplTest {

    @Mock
    private AddressMapper addressMapper;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private AddressServiceImpl addressService;

    @Test
    void create_ShouldReturnResponseDTO() {
        // Given
        AddressRequestDTO requestDTO = new AddressRequestDTO();
        requestDTO.setStreet("Test Street");
        requestDTO.setClientId(1L);

        Client client = new Client();
        client.setClientId(1L);

        Address address = new Address();
        Address savedAddress = new Address();
        AddressResponseDTO responseDTO = new AddressResponseDTO(1L, "Test Street", "City", "State", "12342", "maroc", 1L,"mouad", null, null);

        when(addressRepository.findByStreetIgnoreCase("Test Street")).thenReturn(null);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(addressMapper.toEntity(requestDTO)).thenReturn(address);
        when(addressRepository.save(address)).thenReturn(savedAddress);
        when(addressMapper.toResponseDTO(savedAddress)).thenReturn(responseDTO);

        // When
        AddressResponseDTO result = addressService.createAddress(requestDTO);

        // Then
        assertNotNull(result);
        verify(addressRepository).findByStreetIgnoreCase("Test Street");
        verify(clientRepository).findById(1L);
        verify(addressRepository).save(address);
    }

    @Test
    void update_ShouldReturnResponseDTO() {
        // Given
        Long addressId = 1L;
        AddressRequestDTO requestDTO = new AddressRequestDTO();
        requestDTO.setStreet("Updated Street");
        requestDTO.setClientId(1L);

        Client client = new Client();
        client.setClientId(1L);

        Address existingAddress = new Address();
        Address updatedAddress = new Address();
        AddressResponseDTO responseDTO = new AddressResponseDTO(1L, "Updated Street", "City", "State", "12342", "maroc", 1L,"mouad", null, null);

        when(addressRepository.findById(addressId)).thenReturn(Optional.of(existingAddress));
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(addressMapper.toEntity(requestDTO)).thenReturn(updatedAddress);
        when(addressRepository.save(existingAddress)).thenReturn(updatedAddress);
        when(addressMapper.toResponseDTO(updatedAddress)).thenReturn(responseDTO);

        // When
        AddressResponseDTO result = addressService.updateAddress(addressId, requestDTO);

        // Then
        assertNotNull(result);
        verify(addressRepository).findById(addressId);
        verify(clientRepository).findById(1L);
        verify(addressRepository).save(existingAddress);
    }

    @Test
    void delete_ShouldInvokeRepositoryDelete() {
        // Given
        Long addressId = 1L;
        Address existingAddress = new Address();

        when(addressRepository.findById(addressId)).thenReturn(Optional.of(existingAddress));

        // When
        addressService.deleteAddress(addressId);

        // Then
        verify(addressRepository).findById(addressId);
        verify(addressRepository).deleteById(addressId);
    }

    @Test
    void getById_ShouldReturnResponseDTO() {
        // Given
        Long addressId = 1L;
        Address existingAddress = new Address();
        AddressResponseDTO responseDTO = new AddressResponseDTO(1L, "Test Street", "City", "State", "12342", "maroc", 1L,"mouad", null, null);

        when(addressRepository.findById(addressId)).thenReturn(Optional.of(existingAddress));
        when(addressMapper.toResponseDTO(existingAddress)).thenReturn(responseDTO);

        // When
        AddressResponseDTO result = addressService.getAddressById(addressId);

        // Then
        assertNotNull(result);
        verify(addressRepository).findById(addressId);
    }

}
