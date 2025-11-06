package com.supplychainx.service_livraison.controller;

import com.supplychainx.handler.GlobalSuccessHandler;
import com.supplychainx.service_livraison.dto.ClientRequestDTO;
import com.supplychainx.service_livraison.dto.ClientResponseDTO;
import com.supplychainx.service_livraison.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/clients")
public class ClientController {

    private final ClientService clientService;

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> getClientById(@PathVariable("id") Long id) {
        ClientResponseDTO client = clientService.getClientById(id);
        return ResponseEntity.ok(client);
    }

    @PostMapping
    public ResponseEntity<ClientResponseDTO> createClient(@Valid @RequestBody ClientRequestDTO client) {
        ClientResponseDTO createdClient = clientService.createClient(client);
        return ResponseEntity.ok(createdClient);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> updateClient(@PathVariable("id") Long id, @Valid @RequestBody ClientRequestDTO client) {
        ClientResponseDTO updatedClient = clientService.updateClient(id, client);
        return ResponseEntity.ok(updatedClient);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteClient(@PathVariable("id") Long id) {
        clientService.deleteClient(id);
        return GlobalSuccessHandler.handleDeleted("Client with id " + id + " deleted successfully");
    }

    @GetMapping
    public ResponseEntity<Page<ClientResponseDTO>> getAllClients(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "clientId") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
        Page<ClientResponseDTO> clients = clientService.getAllClients(page, size, sortBy, sortDir);
        return ResponseEntity.ok(clients);
    }

}
