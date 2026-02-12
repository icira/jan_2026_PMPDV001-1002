package com.policymanagementplatform.insurancecoreservice.service;

import com.policymanagementplatform.insurancecoreservice.domain.Client;
import com.policymanagementplatform.insurancecoreservice.domain.ClientIdentifierChange;
import com.policymanagementplatform.insurancecoreservice.domain.ClientType;
import com.policymanagementplatform.insurancecoreservice.exceptions.ConflictException;
import com.policymanagementplatform.insurancecoreservice.exceptions.NotFoundException;
import com.policymanagementplatform.insurancecoreservice.repository.ClientIdentifierChangeRepository;
import com.policymanagementplatform.insurancecoreservice.repository.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ClientService {

    private static final Logger log = LoggerFactory.getLogger(ClientService.class);

    private final ClientRepository clientRepository;
    private final ClientIdentifierChangeRepository changeRepository;

    public ClientService(ClientRepository clientRepository, ClientIdentifierChangeRepository changeRepository) {
        this.clientRepository = clientRepository;
        this.changeRepository = changeRepository;
    }

    @Transactional
    public Client create(ClientType type, String name, String identificationNumber, String email, String phone, String primaryAddress) {
        clientRepository.findByIdentificationNumber(identificationNumber)
                .ifPresent(existing -> { throw new ConflictException("Identification number already exists"); });

        Client client = new Client(type, name, identificationNumber, email, phone, primaryAddress);
        Client saved = clientRepository.save(client);

        log.info("Client created id={} identifier={}", saved.getId(), saved.getIdentificationNumber());
        return saved;
    }

    @Transactional
    public Client update(Long clientId, String name, String email, String phone, String primaryAddress) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new NotFoundException("Client not found"));

        client.updateDetails(name, email, phone, primaryAddress);
        return client;
    }

    @Transactional
    public Client changeIdentifier(Long clientId, String newIdentifier, String changedBy) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new NotFoundException("Client not found"));

        Optional<Client> byNew = clientRepository.findByIdentificationNumber(newIdentifier);
        if (byNew.isPresent() && !byNew.get().getId().equals(clientId)) {
            throw new ConflictException("Identification number already exists");
        }

        String old = client.getIdentificationNumber();
        client.changeIdentificationNumber(newIdentifier);

        changeRepository.save(new ClientIdentifierChange(client, old, newIdentifier, changedBy));
        log.info("Client identifier changed clientId={} old={} new={}", clientId, old, newIdentifier);

        return client;
    }

    @Transactional(readOnly = true)
    public Client get(Long clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new NotFoundException("Client not found"));
    }

    @Transactional(readOnly = true)
    public Page<Client> search(Optional<String> name, Optional<String> identifier, Pageable pageable) {
        if (identifier.isPresent()) {
            // exact match endpoint behavior
            return clientRepository.findByIdentificationNumber(identifier.get())
                    .map(c -> new PageImpl<>(java.util.List.of(c), pageable, 1))
                    .orElseGet(() -> new PageImpl<>(java.util.List.of(), pageable, 0));
        }
        if (name.isPresent()) {
            return clientRepository.findByNameContainingIgnoreCase(name.get(), pageable);
        }
        return clientRepository.findAll(pageable);
    }
}
