package com.policymanagementplatform.insurancecoreservice.service;

import com.policymanagementplatform.insurancecoreservice.domain.Broker;
import com.policymanagementplatform.insurancecoreservice.domain.BrokerStatus;
import com.policymanagementplatform.insurancecoreservice.exceptions.ConflictException;
import com.policymanagementplatform.insurancecoreservice.exceptions.NotFoundException;
import com.policymanagementplatform.insurancecoreservice.repository.BrokerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BrokerServiceTest {

    /*
    LANES ADDED:
    - Unit tests for BrokerAdminService.
    WHY:
    - Covers: unique brokerCode constraint, create, update, changeStatus, get, list.
    */

    private BrokerRepository brokerRepository;
    private BrokerService service;

    @BeforeEach
    void setUp() {
        brokerRepository = mock(BrokerRepository.class);
        service = new BrokerService(brokerRepository);
    }

    @Test
    void createShouldThrowConflictWhenBrokerCodeExists() {
        when(brokerRepository.findByBrokerCodeIgnoreCase("BR-1")).thenReturn(Optional.of(mock(Broker.class)));

        assertThrows(ConflictException.class, () ->
                service.create("BR-1", "Name", "a@b.com", "07", 5.0, BrokerStatus.ACTIVE)
        );
        verify(brokerRepository).findByBrokerCodeIgnoreCase("BR-1");
        verifyNoMoreInteractions(brokerRepository);
    }

    @Test
    void createShouldSaveWhenBrokerCodeIsUnique() {
        when(brokerRepository.findByBrokerCodeIgnoreCase("BR-1")).thenReturn(Optional.empty());
        when(brokerRepository.save(any(Broker.class))).thenAnswer(inv -> inv.getArgument(0));

        Broker created = service.create("BR-1", "Name", "a@b.com", "07", 5.0, BrokerStatus.ACTIVE);

        assertNotNull(created);
        assertEquals("BR-1", created.getBrokerCode());
        assertEquals(BrokerStatus.ACTIVE, created.getStatus());

        verify(brokerRepository).findByBrokerCodeIgnoreCase("BR-1");
        verify(brokerRepository).save(any(Broker.class));
        verifyNoMoreInteractions(brokerRepository);
    }

    @Test
    void getShouldThrowNotFoundWhenMissing() {
        when(brokerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.get(1L));

        verify(brokerRepository).findById(1L);
        verifyNoMoreInteractions(brokerRepository);
    }

    @Test
    void getShouldReturnBrokerWhenExists() {
        Broker broker = mock(Broker.class);
        when(brokerRepository.findById(1L)).thenReturn(Optional.of(broker));

        Broker result = service.get(1L);

        assertSame(broker, result);
        verify(brokerRepository).findById(1L);
        verifyNoMoreInteractions(brokerRepository);
    }

    @Test
    void updateShouldThrowNotFoundWhenMissing() {
        when(brokerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                service.update(1L, "New", "new@b.com", "079", 7.5)
        );

        verify(brokerRepository).findById(1L);
        verifyNoMoreInteractions(brokerRepository);
    }

    @Test
    void updateShouldMutateBrokerAndReturnIt() {
        Broker broker = spy(new Broker("BR-1", "Name", "a@b.com", "07", 5.0, BrokerStatus.ACTIVE));
        when(brokerRepository.findById(1L)).thenReturn(Optional.of(broker));

        Broker updated = service.update(1L, "New", "new@b.com", "079", 7.5);

        assertSame(broker, updated);
        assertEquals("New", broker.getName());
        assertEquals("new@b.com", broker.getEmail());
        assertEquals("079", broker.getPhone());
        assertEquals(7.5, broker.getCommissionPercentage(), 0.0001);

        verify(brokerRepository).findById(1L);
        verifyNoMoreInteractions(brokerRepository);
    }

    @Test
    void changeStatusShouldThrowNotFoundWhenMissing() {
        when(brokerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.changeStatus(1L, BrokerStatus.INACTIVE));

        verify(brokerRepository).findById(1L);
        verifyNoMoreInteractions(brokerRepository);
    }

    @Test
    void changeStatusShouldActivateWhenStatusActiveRequested() {
        Broker broker = spy(new Broker("BR-1", "Name", "a@b.com", "07", 5.0, BrokerStatus.INACTIVE));
        when(brokerRepository.findById(1L)).thenReturn(Optional.of(broker));

        Broker updated = service.changeStatus(1L, BrokerStatus.ACTIVE);

        assertEquals(BrokerStatus.ACTIVE, updated.getStatus());
        verify(broker).activate();
    }

    @Test
    void changeStatusShouldDeactivateWhenStatusInactiveRequested() {
        Broker broker = spy(new Broker("BR-1", "Name", "a@b.com", "07", 5.0, BrokerStatus.ACTIVE));
        when(brokerRepository.findById(1L)).thenReturn(Optional.of(broker));

        Broker updated = service.changeStatus(1L, BrokerStatus.INACTIVE);

        assertEquals(BrokerStatus.INACTIVE, updated.getStatus());
        verify(broker).deactivate();
    }

    @Test
    void listShouldDelegateToRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Broker> page = new PageImpl<>(List.of(mock(Broker.class)), pageable, 1);

        when(brokerRepository.findAll(pageable)).thenReturn(page);

        Page<Broker> result = service.list(pageable);

        assertSame(page, result);
        verify(brokerRepository).findAll(pageable);
        verifyNoMoreInteractions(brokerRepository);
    }
}
