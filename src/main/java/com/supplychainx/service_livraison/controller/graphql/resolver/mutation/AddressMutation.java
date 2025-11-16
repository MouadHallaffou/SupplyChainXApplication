package com.supplychainx.service_livraison.controller.graphql.resolver.mutation;

import com.supplychainx.handler.GlobalSuccessHandler;
import com.supplychainx.service_livraison.dto.Request.AddressRequestDTO;
import com.supplychainx.service_livraison.dto.Response.AddressResponseDTO;
import com.supplychainx.service_livraison.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AddressMutation {

    private final AddressService addressService;

    @MutationMapping
    public AddressResponseDTO createAddress(@Valid @Argument AddressRequestDTO input) {
        return addressService.createAddress(input);
    }

    @MutationMapping
    public AddressResponseDTO updateAddress(@Valid @Argument Long id, @Argument AddressRequestDTO input) {
        return addressService.updateAddress(id, input);
    }

    @MutationMapping
    //    public ResponseEntity<Map<String, Object>> deleteAddress(@Argument Long id) {
    //        addressService.deleteAddress(id);
    //        return GlobalSuccessHandler.handleDeleted("Address deleted successfully.");
    //    }
    public Boolean deleteAddress(@Argument Long id) {
        addressService.deleteAddress(id);
        return true;
    }

}
