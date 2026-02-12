package com.policymanagementplatform.insurancecoreservice.web;

import com.policymanagementplatform.insurancecoreservice.domain.Client;
import com.policymanagementplatform.insurancecoreservice.service.ClientService;
import com.policymanagementplatform.insurancecoreservice.web.dto.ChangeClientIdentifierRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/brokers/clients")
public class BrokerClientIdentifierController {

    private final ClientService clientService;

    public BrokerClientIdentifierController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PutMapping("/{clientId}/identifier")
    public Client changeIdentifier(@PathVariable Long clientId, @Valid @RequestBody ChangeClientIdentifierRequest req) {
        // "changedBy" is placeholder until auth is implemented.
        return clientService.changeIdentifier(clientId, req.newIdentificationNumber(), "SYSTEM");
    }
}
