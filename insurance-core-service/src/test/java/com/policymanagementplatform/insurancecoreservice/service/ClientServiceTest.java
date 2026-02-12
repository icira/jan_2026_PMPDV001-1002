package com.policymanagementplatform.insurancecoreservice.service;

import com.policymanagementplatform.insurancecoreservice.domain.Client;
import com.policymanagementplatform.insurancecoreservice.domain.ClientIdentifierChange;
import com.policymanagementplatform.insurancecoreservice.domain.ClientType;
import com.policymanagementplatform.insurancecoreservice.exceptions.ConflictException;
import com.policymanagementplatform.insurancecoreservice.exceptions.NotFoundException;
import com.policymanagementplatform.insurancecoreservice.repository.ClientIdentifierChangeRepository;
import com.policymanagementplatform.insurancecoreservice.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    /*
    LANES ADDED:
    - Mock dependencies injected into ClientService.
    WHY:
    - Unit tests isolate service logic from database and other infrastructure.
    */
    @Mock private ClientRepository clientRepository;
    @Mock private ClientIdentifierChangeRepository changeRepository;

    /*
    LANES ADDED:
    - Service under test.
    WHY:
    - This is the class whose behavior we verify.
    */
    private ClientService clientService;

    @BeforeEach
    void setUp() {
        clientService = new ClientService(clientRepository, changeRepository);
    }

    // -----------------------
    // create(...)
    // -----------------------

    @Test
    void createShouldSaveClientWhenIdentificationNumberIsUnique() {
        // Arrange
        when(clientRepository.findByIdentificationNumber("ID-1")).thenReturn(Optional.empty());

        Client saved = mock(Client.class);
        when(saved.getId()).thenReturn(1L);
        when(saved.getIdentificationNumber()).thenReturn("ID-1");

        // We capture the Client instance passed to save() to ensure it's not null and save() is called.
        when(clientRepository.save(any(Client.class))).thenReturn(saved);

        // Act
        Client result = clientService.create(
                ClientType.INDIVIDUAL,
                "John Doe",
                "ID-1",
                "john@doe.com",
                "0700000000",
                "Some Address"
        );

        // Assert
        assertSame(saved, result);

        verify(clientRepository).findByIdentificationNumber("ID-1");
        ArgumentCaptor<Client> captor = ArgumentCaptor.forClass(Client.class);
        verify(clientRepository).save(captor.capture());
        assertNotNull(captor.getValue(), "Client passed to save() must not be null");

        verifyNoInteractions(changeRepository);
        verifyNoMoreInteractions(clientRepository);
    }

    @Test
    void createShouldThrowConflictWhenIdentificationNumberAlreadyExists() {
        // Arrange
        when(clientRepository.findByIdentificationNumber("DUP")).thenReturn(Optional.of(mock(Client.class)));

        // Act + Assert
        ConflictException ex = assertThrows(ConflictException.class, () ->
                clientService.create(ClientType.COMPANY, "ACME", "DUP", null, null, null)
        );

        assertEquals("Identification number already exists", ex.getMessage());

        verify(clientRepository).findByIdentificationNumber("DUP");
        verify(clientRepository, never()).save(any());
        verifyNoInteractions(changeRepository);
        verifyNoMoreInteractions(clientRepository);
    }

    // -----------------------
    // update(...)
    // -----------------------

    @Test
    void updateShouldUpdateClientDetailsWhenClientExists() {
        // Arrange
        Client client = mock(Client.class);
        when(clientRepository.findById(10L)).thenReturn(Optional.of(client));

        // Act
        Client result = clientService.update(10L, "New Name", "a@b.com", "0711", "New Address");

        // Assert
        assertSame(client, result);

        verify(clientRepository).findById(10L);
        verify(client).updateDetails("New Name", "a@b.com", "0711", "New Address");

        verifyNoInteractions(changeRepository);
        verifyNoMoreInteractions(clientRepository, client);
    }

    @Test
    void updateShouldThrowNotFoundWhenClientDoesNotExist() {
        // Arrange
        when(clientRepository.findById(404L)).thenReturn(Optional.empty());

        // Act + Assert
        NotFoundException ex = assertThrows(NotFoundException.class, () ->
                clientService.update(404L, "x", "y", "z", "a")
        );

        assertEquals("Client not found", ex.getMessage());

        verify(clientRepository).findById(404L);
        verifyNoInteractions(changeRepository);
        verifyNoMoreInteractions(clientRepository);
    }

    // -----------------------
    // changeIdentifier(...)
    // -----------------------

    @Test
    void changeIdentifierShouldThrowNotFoundWhenClientDoesNotExist() {
        // Arrange
        when(clientRepository.findById(404L)).thenReturn(Optional.empty());

        // Act + Assert
        NotFoundException ex = assertThrows(NotFoundException.class, () ->
                clientService.changeIdentifier(404L, "NEW", "SYSTEM")
        );

        assertEquals("Client not found", ex.getMessage());

        verify(clientRepository).findById(404L);
        verifyNoMoreInteractions(clientRepository);
        verifyNoInteractions(changeRepository);
    }

    @Test
    void changeIdentifierShouldThrowConflictWhenNewIdentifierBelongsToAnotherClient() {
        // Arrange
        Long clientId = 1L;

        Client client = mock(Client.class);
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        // Another client already has the new identifier
        Client otherClient = mock(Client.class);
        when(otherClient.getId()).thenReturn(2L);
        when(clientRepository.findByIdentificationNumber("NEW")).thenReturn(Optional.of(otherClient));

        // Act + Assert
        ConflictException ex = assertThrows(ConflictException.class, () ->
                clientService.changeIdentifier(clientId, "NEW", "SYSTEM")
        );

        assertEquals("Identification number already exists", ex.getMessage());

        // Verify interactions (service must stop before touching the current client fields)
        verify(clientRepository).findById(clientId);
        verify(clientRepository).findByIdentificationNumber("NEW");

        verify(client, never()).getIdentificationNumber();
        verify(client, never()).changeIdentificationNumber(anyString());
        verifyNoInteractions(changeRepository);

        verifyNoMoreInteractions(clientRepository, client, otherClient);
    }


    @Test
    void changeIdentifierShouldAllowWhenNewIdentifierBelongsToSameClient() {
        // Arrange
        Long clientId = 1L;
        Client client = mock(Client.class);
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(client.getIdentificationNumber()).thenReturn("OLD");

        // Repository returns the same client id for the "new" identifier
        Client sameClient = mock(Client.class);
        when(sameClient.getId()).thenReturn(clientId);
        when(clientRepository.findByIdentificationNumber("NEW")).thenReturn(Optional.of(sameClient));

        // Act
        Client result = clientService.changeIdentifier(clientId, "NEW", "SYSTEM");

        // Assert
        assertSame(client, result);

        verify(clientRepository).findById(clientId);
        verify(clientRepository).findByIdentificationNumber("NEW");

        verify(client).getIdentificationNumber();
        verify(client).changeIdentificationNumber("NEW");

        // Ensure audit entry is saved
        ArgumentCaptor<ClientIdentifierChange> changeCaptor = ArgumentCaptor.forClass(ClientIdentifierChange.class);
        verify(changeRepository).save(changeCaptor.capture());
        assertNotNull(changeCaptor.getValue(), "Saved audit entity must not be null");

        verifyNoMoreInteractions(clientRepository, client, sameClient, changeRepository);
    }

    @Test
    void changeIdentifierShouldUpdateAndAuditWhenNewIdentifierIsFree() {
        // Arrange
        Long clientId = 10L;
        Client client = mock(Client.class);
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(client.getIdentificationNumber()).thenReturn("OLD");

        when(clientRepository.findByIdentificationNumber("NEW")).thenReturn(Optional.empty());

        // Act
        Client result = clientService.changeIdentifier(clientId, "NEW", "adminUser");

        // Assert
        assertSame(client, result);

        verify(clientRepository).findById(clientId);
        verify(clientRepository).findByIdentificationNumber("NEW");
        verify(client).getIdentificationNumber();
        verify(client).changeIdentificationNumber("NEW");

        ArgumentCaptor<ClientIdentifierChange> captor = ArgumentCaptor.forClass(ClientIdentifierChange.class);
        verify(changeRepository).save(captor.capture());
        assertNotNull(captor.getValue());

        verifyNoMoreInteractions(clientRepository, client, changeRepository);
    }

    // -----------------------
    // get(...)
    // -----------------------

    @Test
    void getShouldReturnClientWhenExists() {
        // Arrange
        Client client = mock(Client.class);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        // Act
        Client result = clientService.get(1L);

        // Assert
        assertSame(client, result);

        verify(clientRepository).findById(1L);
        verifyNoInteractions(changeRepository);
        verifyNoMoreInteractions(clientRepository);
    }

    @Test
    void getShouldThrowNotFoundWhenMissing() {
        // Arrange
        when(clientRepository.findById(404L)).thenReturn(Optional.empty());

        // Act + Assert
        NotFoundException ex = assertThrows(NotFoundException.class, () -> clientService.get(404L));
        assertEquals("Client not found", ex.getMessage());

        verify(clientRepository).findById(404L);
        verifyNoInteractions(changeRepository);
        verifyNoMoreInteractions(clientRepository);
    }

    // -----------------------
    // search(...)
    // -----------------------

    @Test
    void searchShouldReturnExactMatchPageWhenIdentifierPresentAndFound() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Client client = mock(Client.class);

        when(clientRepository.findByIdentificationNumber("ID-1")).thenReturn(Optional.of(client));

        // Act
        Page<Client> result = clientService.search(Optional.empty(), Optional.of("ID-1"), pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertSame(client, result.getContent().getFirst());

        verify(clientRepository).findByIdentificationNumber("ID-1");
        verifyNoMoreInteractions(clientRepository);
        verifyNoInteractions(changeRepository);
    }

    @Test
    void searchShouldReturnEmptyPageWhenIdentifierPresentAndNotFound() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        when(clientRepository.findByIdentificationNumber("MISSING")).thenReturn(Optional.empty());

        // Act
        Page<Client> result = clientService.search(Optional.empty(), Optional.of("MISSING"), pageable);

        // Assert
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());

        verify(clientRepository).findByIdentificationNumber("MISSING");
        verifyNoMoreInteractions(clientRepository);
        verifyNoInteractions(changeRepository);
    }

    @Test
    void searchShouldSearchByNameWhenNamePresentAndIdentifierAbsent() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 5);
        Page<Client> page = new PageImpl<>(List.of(mock(Client.class)), pageable, 1);

        when(clientRepository.findByNameContainingIgnoreCase("john", pageable)).thenReturn(page);

        // Act
        Page<Client> result = clientService.search(Optional.of("john"), Optional.empty(), pageable);

        // Assert
        assertSame(page, result);

        verify(clientRepository).findByNameContainingIgnoreCase("john", pageable);
        verifyNoMoreInteractions(clientRepository);
        verifyNoInteractions(changeRepository);
    }

    @Test
    void searchShouldReturnAllWhenNoFiltersProvided() {
        // Arrange
        Pageable pageable = PageRequest.of(1, 20);
        Page<Client> page = new PageImpl<>(List.of(mock(Client.class), mock(Client.class)), pageable, 2);

        when(clientRepository.findAll(pageable)).thenReturn(page);

        // Act
        Page<Client> result = clientService.search(Optional.empty(), Optional.empty(), pageable);

        // Assert
        assertSame(page, result);

        verify(clientRepository).findAll(pageable);
        verifyNoMoreInteractions(clientRepository);
        verifyNoInteractions(changeRepository);
    }
}
