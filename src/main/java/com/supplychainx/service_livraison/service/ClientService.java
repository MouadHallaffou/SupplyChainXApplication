package com.supplychainx.service_livraison.service;

import com.supplychainx.service_livraison.dto.Request.ClientRequestDTO;
import com.supplychainx.service_livraison.dto.Response.ClientResponseDTO;
import org.springframework.data.domain.Page;

public interface ClientService {
    ClientResponseDTO getClientById(Long clientId);
    ClientResponseDTO createClient(ClientRequestDTO clientRequestDTO);
    ClientResponseDTO updateClient(Long clientId, ClientRequestDTO clientRequestDTO);
    void deleteClient(Long clientId);
    Page<ClientResponseDTO> getAllClients(int page, int size, String sortBy, String direction);
}
