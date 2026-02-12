package com.policymanagementplatform.insurancecoreservice.web;

import com.policymanagementplatform.insurancecoreservice.domain.Client;
import com.policymanagementplatform.insurancecoreservice.service.ClientService;
import com.policymanagementplatform.insurancecoreservice.web.dto.CreateClientRequest;
import com.policymanagementplatform.insurancecoreservice.web.dto.UpdateClientRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/brokers/clients")
public class BrokerClientController {

    private final ClientService clientService;

    public BrokerClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public Page<Client> search(
            @RequestParam Optional<String> name,
            @RequestParam Optional<String> identifier,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return clientService.search(name, identifier, pageable);
    }

    @GetMapping("/{clientId}")
    public Client get(@PathVariable Long clientId) {
        return clientService.get(clientId);
    }

    @PostMapping
    public ResponseEntity<Client> create(@Valid @RequestBody CreateClientRequest req) {
        Client created = clientService.create(
                req.type(), req.name(), req.identificationNumber(), req.email(), req.phone(), req.primaryAddress()
        );
        return ResponseEntity
                .created(URI.create("/api/brokers/clients/" + created.getId()))
                .body(created);
    }

    @PutMapping("/{clientId}")
    public Client update(@PathVariable Long clientId, @Valid @RequestBody UpdateClientRequest req) {
        return clientService.update(clientId, req.name(), req.email(), req.phone(), req.primaryAddress());
    }
}
