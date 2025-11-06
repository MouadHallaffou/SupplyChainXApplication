package com.supplychainx.service_livraison.controller;

import com.supplychainx.handler.GlobalSuccessHandler;
import com.supplychainx.service_livraison.dto.AddressRequestDTO;
import com.supplychainx.service_livraison.dto.AddressResponseDTO;
import com.supplychainx.service_livraison.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/livraison/addresses")
public class AddressController {

    private final AddressService addressService;

    @GetMapping("/{id}")
    public AddressResponseDTO getAddress(@PathVariable("id") Long id) {
        return addressService.getAddressById(id);
    }

    @PostMapping
    public ResponseEntity<AddressResponseDTO> createAddress(@Valid @RequestBody AddressRequestDTO addressRequestDTO) {
        AddressResponseDTO responseDTO = addressService.createAddress(addressRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> updateAddress(@PathVariable("id") Long id,
                                                            @Valid @RequestBody AddressRequestDTO addressRequestDTO) {
        AddressResponseDTO addressResponseDTO = addressService.updateAddress(id, addressRequestDTO);
        return ResponseEntity.ok(addressResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteAddress(@PathVariable("id") Long id) {
        addressService.deleteAddress(id);
        return GlobalSuccessHandler.handleDeleted("Address with id " + id + " deleted successfully");
    }

    @GetMapping
    public ResponseEntity<Page<AddressResponseDTO>> getAllAddresses(@RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size) {
        Page<AddressResponseDTO> addresses = addressService.getAllAddresses(page, size);
        return ResponseEntity.ok(addresses);
    }
}
