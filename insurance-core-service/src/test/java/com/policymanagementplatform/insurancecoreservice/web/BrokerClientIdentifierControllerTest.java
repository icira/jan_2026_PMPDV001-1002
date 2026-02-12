package com.policymanagementplatform.insurancecoreservice.web;

import com.policymanagementplatform.insurancecoreservice.domain.Client;
import com.policymanagementplatform.insurancecoreservice.service.ClientService;
import com.policymanagementplatform.insurancecoreservice.web.dto.ChangeClientIdentifierRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BrokerClientIdentifierControllerTest {

    /*
    LANES ADDED:
    - Mock ClientService dependency.
    WHY:
    - Unit tests isolate controller logic from service layer.
    */
    private ClientService clientService;

    /*
    LANES ADDED:
    - Controller under test.
    WHY:
    - We call controller method directly (no Spring context required).
    */
    private BrokerClientIdentifierController controller;

    @BeforeEach
    void setUp() {
        clientService = mock(ClientService.class);
        controller = new BrokerClientIdentifierController(clientService);
    }

    @Test
    void changeIdentifierShouldDelegateToServiceUsingSystemChangedBy() {
        // Arrange
        Long clientId = 10L;
        ChangeClientIdentifierRequest req = new ChangeClientIdentifierRequest("NEW-ID");

        Client expected = mock(Client.class);
        when(clientService.changeIdentifier(clientId, "NEW-ID", "SYSTEM")).thenReturn(expected);

        // Act
        Client result = controller.changeIdentifier(clientId, req);

        // Assert
        assertSame(expected, result);

        verify(clientService).changeIdentifier(clientId, "NEW-ID", "SYSTEM");
        verifyNoMoreInteractions(clientService);
    }
}
