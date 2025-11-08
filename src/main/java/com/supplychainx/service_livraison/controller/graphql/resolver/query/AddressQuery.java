package com.supplychainx.service_livraison.controller.graphql.resolver.query;

import com.supplychainx.service_livraison.dto.Response.AddressResponseDTO;
import com.supplychainx.service_livraison.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class AddressQuery {

    private final AddressService addressService;

    @QueryMapping
    public Page<AddressResponseDTO> getAllAddresses(
            @Argument Integer page,
            @Argument Integer size,
            @Argument String sortBy,
            @Argument String sortDir) {
        return addressService.getAllAddresses(page, size, sortBy, sortDir);
    }

    @QueryMapping
    public AddressResponseDTO getAddressById(@Argument Long id) {
        return addressService.getAddressById(id);
    }
}
