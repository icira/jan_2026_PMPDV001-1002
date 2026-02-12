package com.policymanagementplatform.insurancecoreservice.web;

import com.policymanagementplatform.insurancecoreservice.domain.Client;
import com.policymanagementplatform.insurancecoreservice.domain.ClientType;
import com.policymanagementplatform.insurancecoreservice.service.ClientService;
import com.policymanagementplatform.insurancecoreservice.web.dto.CreateClientRequest;
import com.policymanagementplatform.insurancecoreservice.web.dto.UpdateClientRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BrokerClientControllerTest {

    /*
    LANES ADDED:
    - Mock ClientService.
    WHY:
    - Unit test isolates controller logic from service layer.
    */
    private ClientService clientService;

    /*
    LANES ADDED:
    - Controller under test.
    WHY:
    - We call controller methods directly (fast, deterministic).
    */
    private BrokerClientController controller;

    @BeforeEach
    void setUp() {
        clientService = mock(ClientService.class);
        controller = new BrokerClientController(clientService);
    }

    // ------------------------
    // search(...)
    // ------------------------

    @Test
    void searchShouldCreatePageRequestWithSortAndDelegateToService() {
        // Arrange
        Optional<String> name = Optional.of("john");
        Optional<String> identifier = Optional.empty();
        int page = 1;
        int size = 25;

        Pageable expectedPageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Client> expectedPage = new PageImpl<>(List.of(mock(Client.class)), expectedPageable, 1);

        when(clientService.search(eq(name), eq(identifier), any(Pageable.class))).thenReturn(expectedPage);

        // Act
        Page<Client> result = controller.search(name, identifier, page, size);

        // Assert
        assertSame(expectedPage, result);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(clientService).search(eq(name), eq(identifier), pageableCaptor.capture());

        Pageable actual = pageableCaptor.getValue();
        assertNotNull(actual);
        assertEquals(page, actual.getPageNumber());
        assertEquals(size, actual.getPageSize());

        Sort.Order idOrder = actual.getSort().getOrderFor("id");
        assertNotNull(idOrder, "Sort must contain an order for 'id'");
        assertEquals(Sort.Direction.DESC, idOrder.getDirection());

        verifyNoMoreInteractions(clientService);
    }

    // ------------------------
    // get(...)
    // ------------------------

    @Test
    void getShouldDelegateToServiceAndReturnClient() {
        // Arrange
        Long clientId = 10L;
        Client client = mock(Client.class);
        when(clientService.get(clientId)).thenReturn(client);

        // Act
        Client result = controller.get(clientId);

        // Assert
        assertSame(client, result);

        verify(clientService).get(clientId);
        verifyNoMoreInteractions(clientService);
    }

    // ------------------------
    // create(...)
    // ------------------------

    @Test
    void createShouldDelegateToServiceAndReturnCreatedResponseWithLocation() {
        // Arrange
        CreateClientRequest req = new CreateClientRequest(
                ClientType.INDIVIDUAL,
                "John Doe",
                "CNP-1",
                "john@doe.com",
                "0700000000",
                "Some Address"
        );

        Client created = mock(Client.class);
        when(created.getId()).thenReturn(123L);

        when(clientService.create(
                eq(req.type()),
                eq(req.name()),
                eq(req.identificationNumber()),
                eq(req.email()),
                eq(req.phone()),
                eq(req.primaryAddress())
        )).thenReturn(created);

        // Act
        var response = controller.create(req);

        // Assert
        assertEquals(201, response.getStatusCode().value());
        assertEquals(URI.create("/api/brokers/clients/123"), response.getHeaders().getLocation());
        assertSame(created, response.getBody());

        verify(clientService).create(
                req.type(),
                req.name(),
                req.identificationNumber(),
                req.email(),
                req.phone(),
                req.primaryAddress()
        );
        verifyNoMoreInteractions(clientService);
    }

    // ------------------------
    // update(...)
    // ------------------------

    @Test
    void updateShouldDelegateToServiceUsingRequestFieldsAndReturnClient() {
        // Arrange
        Long clientId = 77L;

        UpdateClientRequest req = new UpdateClientRequest(
                "New Name",
                "new@mail.com",
                "0711",
                "New Address"
        );

        Client updated = mock(Client.class);
        when(clientService.update(
                eq(clientId),
                eq(req.name()),
                eq(req.email()),
                eq(req.phone()),
                eq(req.primaryAddress())
        )).thenReturn(updated);

        // Act
        Client result = controller.update(clientId, req);

        // Assert
        assertSame(updated, result);

        verify(clientService).update(
                clientId,
                req.name(),
                req.email(),
                req.phone(),
                req.primaryAddress()
        );
        verifyNoMoreInteractions(clientService);
    }
}

