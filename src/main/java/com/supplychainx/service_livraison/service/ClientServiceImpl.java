package com.supplychainx.service_livraison.service;

import com.supplychainx.exception.ResourceNotFoundException;
import com.supplychainx.service_livraison.dto.Request.ClientRequestDTO;
import com.supplychainx.service_livraison.dto.Response.ClientResponseDTO;
import com.supplychainx.service_livraison.mapper.ClientMapper;
import com.supplychainx.service_livraison.model.Client;
import com.supplychainx.service_livraison.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Override
    public ClientResponseDTO getClientById(Long clientId) {
        Client client = clientRepository.findById(clientId).orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + clientId));
        return clientMapper.toResponseDTO(client);
    }

    @Override
    @Transactional
    public ClientResponseDTO createClient(ClientRequestDTO clientRequestDTO) {
        Client client = clientMapper.toEntity(clientRequestDTO);
        Client savedClient = clientRepository.save(client);
        return clientMapper.toResponseDTO(savedClient);
    }

    @Override
    @Transactional
    public ClientResponseDTO updateClient(Long clientId, ClientRequestDTO clientRequestDTO) {
        Client existingClient = clientRepository.findById(clientId).orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + clientId));
        clientMapper.toUpdateEntity(clientRequestDTO, existingClient);
        Client updatedClient = clientRepository.save(existingClient);
        return clientMapper.toResponseDTO(updatedClient);
    }

    @Override
    @Transactional
    public void deleteClient(Long clientId) {
        Client existingClient = clientRepository.findById(clientId).orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + clientId));
        clientRepository.delete(existingClient);
    }

    @Override
    public Page<ClientResponseDTO> getAllClients(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<Client> clientPage = clientRepository.findAll(pageRequest);
        return clientPage.map(clientMapper::toResponseDTO);
    }
}
