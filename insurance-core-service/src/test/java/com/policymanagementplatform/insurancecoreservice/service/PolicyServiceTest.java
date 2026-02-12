package com.policymanagementplatform.insurancecoreservice.service;

import com.policymanagementplatform.insurancecoreservice.domain.*;
import com.policymanagementplatform.insurancecoreservice.exceptions.ConflictException;
import com.policymanagementplatform.insurancecoreservice.exceptions.NotFoundException;
import com.policymanagementplatform.insurancecoreservice.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PolicyServiceTest {

    /*
    LANES ADDED:
    - Unit tests for PolicyService (Part 2): createDraft / activate / cancel / get / search.
    WHY:
    - Part 2 requires strict business constraints and correct lifecycle rules.
    */

    private PolicyRepository policyRepository;
    private ClientRepository clientRepository;
    private BuildingRepository buildingRepository;
    private BrokerRepository brokerRepository;
    private CurrencyRepository currencyRepository;
    private PolicyCalculationService calculationService;

    private PolicyService policyService;

    @BeforeEach
    void setUp() {
        policyRepository = mock(PolicyRepository.class);
        clientRepository = mock(ClientRepository.class);
        buildingRepository = mock(BuildingRepository.class);
        brokerRepository = mock(BrokerRepository.class);
        currencyRepository = mock(CurrencyRepository.class);
        calculationService = mock(PolicyCalculationService.class);

        policyService = new PolicyService(
                policyRepository,
                clientRepository,
                buildingRepository,
                brokerRepository,
                currencyRepository,
                calculationService
        );
    }

    @Test
    void createDraftShouldCreateWhenInputsValid() {
        LocalDate start = LocalDate.of(2026, 3, 1);
        LocalDate end = LocalDate.of(2027, 3, 1);

        Client client = mock(Client.class);
        when(client.getId()).thenReturn(1L);

        Building building = mock(Building.class);
        Client owner = mock(Client.class);
        when(owner.getId()).thenReturn(1L);
        when(building.getOwner()).thenReturn(owner);

        Broker broker = mock(Broker.class);
        when(broker.getStatus()).thenReturn(BrokerStatus.ACTIVE);

        Currency currency = mock(Currency.class);
        when(currency.isActive()).thenReturn(true);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(buildingRepository.findById(2L)).thenReturn(Optional.of(building));
        when(brokerRepository.findById(3L)).thenReturn(Optional.of(broker));
        when(currencyRepository.findById(4L)).thenReturn(Optional.of(currency));

        when(calculationService.calculateFinalPremium(1000.0, broker, building, start)).thenReturn(1234.0);

        when(policyRepository.save(any(Policy.class))).thenAnswer(inv -> inv.getArgument(0));

        Policy created = policyService.createDraft(1L, 2L, 4L, 1000.0, start, end, 3L);

        assertNotNull(created.getPolicyNumber());
        assertEquals(PolicyStatus.DRAFT, created.getStatus());
        assertEquals(1000.0, created.getBasePremium(), 0.0001);
        assertEquals(1234.0, created.getFinalPremium(), 0.0001);

        verify(calculationService).calculateFinalPremium(1000.0, broker, building, start);
        verify(policyRepository).save(any(Policy.class));
    }

    @Test
    void createDraftShouldThrowNotFoundWhenClientMissing() {
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                policyService.createDraft(1L, 2L, 4L, 1000.0,
                        LocalDate.of(2026, 3, 1), LocalDate.of(2027, 3, 1), 3L)
        );

        verifyNoInteractions(buildingRepository, brokerRepository, currencyRepository, calculationService, policyRepository);
    }

    @Test
    void createDraftShouldThrowNotFoundWhenBuildingMissing() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(mock(Client.class)));
        when(buildingRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                policyService.createDraft(1L, 2L, 4L, 1000.0,
                        LocalDate.of(2026, 3, 1), LocalDate.of(2027, 3, 1), 3L)
        );

        verifyNoInteractions(brokerRepository, currencyRepository, calculationService, policyRepository);
    }

    @Test
    void createDraftShouldRejectWhenBuildingDoesNotBelongToClient() {
        Client client = mock(Client.class);
        when(client.getId()).thenReturn(1L);

        Building building = mock(Building.class);
        Client owner = mock(Client.class);
        when(owner.getId()).thenReturn(999L);
        when(building.getOwner()).thenReturn(owner);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(buildingRepository.findById(2L)).thenReturn(Optional.of(building));

        assertThrows(ConflictException.class, () ->
                policyService.createDraft(1L, 2L, 4L, 1000.0,
                        LocalDate.of(2026, 3, 1), LocalDate.of(2027, 3, 1), 3L)
        );

        verifyNoInteractions(brokerRepository, currencyRepository, calculationService, policyRepository);
    }

    @Test
    void createDraftShouldThrowNotFoundWhenBrokerMissing() {
        Client client = mock(Client.class);
        when(client.getId()).thenReturn(1L);

        Building building = mock(Building.class);
        Client owner = mock(Client.class);
        when(owner.getId()).thenReturn(1L);
        when(building.getOwner()).thenReturn(owner);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(buildingRepository.findById(2L)).thenReturn(Optional.of(building));
        when(brokerRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                policyService.createDraft(1L, 2L, 4L, 1000.0,
                        LocalDate.of(2026, 3, 1), LocalDate.of(2027, 3, 1), 3L)
        );

        verifyNoInteractions(currencyRepository, calculationService, policyRepository);
    }

    @Test
    void createDraftShouldRejectWhenBrokerInactive() {
        Client client = mock(Client.class);
        when(client.getId()).thenReturn(1L);

        Building building = mock(Building.class);
        Client owner = mock(Client.class);
        when(owner.getId()).thenReturn(1L);
        when(building.getOwner()).thenReturn(owner);

        Broker broker = mock(Broker.class);
        when(broker.getStatus()).thenReturn(BrokerStatus.INACTIVE);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(buildingRepository.findById(2L)).thenReturn(Optional.of(building));
        when(brokerRepository.findById(3L)).thenReturn(Optional.of(broker));

        assertThrows(ConflictException.class, () ->
                policyService.createDraft(1L, 2L, 4L, 1000.0,
                        LocalDate.of(2026, 3, 1), LocalDate.of(2027, 3, 1), 3L)
        );

        verifyNoInteractions(currencyRepository, calculationService, policyRepository);
    }

    @Test
    void createDraftShouldThrowNotFoundWhenCurrencyMissing() {
        Client client = mock(Client.class);
        when(client.getId()).thenReturn(1L);

        Building building = mock(Building.class);
        Client owner = mock(Client.class);
        when(owner.getId()).thenReturn(1L);
        when(building.getOwner()).thenReturn(owner);

        Broker broker = mock(Broker.class);
        when(broker.getStatus()).thenReturn(BrokerStatus.ACTIVE);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(buildingRepository.findById(2L)).thenReturn(Optional.of(building));
        when(brokerRepository.findById(3L)).thenReturn(Optional.of(broker));
        when(currencyRepository.findById(4L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                policyService.createDraft(1L, 2L, 4L, 1000.0,
                        LocalDate.of(2026, 3, 1), LocalDate.of(2027, 3, 1), 3L)
        );

        verifyNoInteractions(calculationService, policyRepository);
    }

    @Test
    void createDraftShouldRejectWhenCurrencyInactive() {
        Client client = mock(Client.class);
        when(client.getId()).thenReturn(1L);

        Building building = mock(Building.class);
        Client owner = mock(Client.class);
        when(owner.getId()).thenReturn(1L);
        when(building.getOwner()).thenReturn(owner);

        Broker broker = mock(Broker.class);
        when(broker.getStatus()).thenReturn(BrokerStatus.ACTIVE);

        Currency currency = mock(Currency.class);
        when(currency.isActive()).thenReturn(false);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(buildingRepository.findById(2L)).thenReturn(Optional.of(building));
        when(brokerRepository.findById(3L)).thenReturn(Optional.of(broker));
        when(currencyRepository.findById(4L)).thenReturn(Optional.of(currency));

        assertThrows(ConflictException.class, () ->
                policyService.createDraft(1L, 2L, 4L, 1000.0,
                        LocalDate.of(2026, 3, 1), LocalDate.of(2027, 3, 1), 3L)
        );

        verifyNoInteractions(calculationService, policyRepository);
    }

    @Test
    void createDraftShouldRejectWhenBasePremiumNotPositive() {
        Client client = mock(Client.class);
        when(client.getId()).thenReturn(1L);

        Building building = mock(Building.class);
        Client owner = mock(Client.class);
        when(owner.getId()).thenReturn(1L);
        when(building.getOwner()).thenReturn(owner);

        Broker broker = mock(Broker.class);
        when(broker.getStatus()).thenReturn(BrokerStatus.ACTIVE);

        Currency currency = mock(Currency.class);
        when(currency.isActive()).thenReturn(true);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(buildingRepository.findById(2L)).thenReturn(Optional.of(building));
        when(brokerRepository.findById(3L)).thenReturn(Optional.of(broker));
        when(currencyRepository.findById(4L)).thenReturn(Optional.of(currency));

        assertThrows(ConflictException.class, () ->
                policyService.createDraft(1L, 2L, 4L, 0.0,
                        LocalDate.of(2026, 3, 1), LocalDate.of(2027, 3, 1), 3L)
        );

        verifyNoInteractions(calculationService, policyRepository);
    }

    @Test
    void createDraftShouldRejectWhenEndDateBeforeStartDate() {
        Client client = mock(Client.class);
        when(client.getId()).thenReturn(1L);

        Building building = mock(Building.class);
        Client owner = mock(Client.class);
        when(owner.getId()).thenReturn(1L);
        when(building.getOwner()).thenReturn(owner);

        Broker broker = mock(Broker.class);
        when(broker.getStatus()).thenReturn(BrokerStatus.ACTIVE);

        Currency currency = mock(Currency.class);
        when(currency.isActive()).thenReturn(true);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(buildingRepository.findById(2L)).thenReturn(Optional.of(building));
        when(brokerRepository.findById(3L)).thenReturn(Optional.of(broker));
        when(currencyRepository.findById(4L)).thenReturn(Optional.of(currency));

        assertThrows(ConflictException.class, () ->
                policyService.createDraft(1L, 2L, 4L, 1000.0,
                        LocalDate.of(2027, 3, 1), LocalDate.of(2026, 3, 1), 3L)
        );

        verifyNoInteractions(calculationService, policyRepository);
    }

    @Test
    void activateShouldThrowNotFoundWhenPolicyMissing() {
        when(policyRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> policyService.activate(1L, LocalDate.of(2026, 2, 10)));
        verify(policyRepository).expireActivePolicies(LocalDate.of(2026, 2, 10));
    }

    @Test
    void activateShouldRejectWhenStartDateInPast() {
        Policy policy = mock(Policy.class);
        when(policy.getStartDate()).thenReturn(LocalDate.of(2026, 1, 1));

        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));

        assertThrows(ConflictException.class, () -> policyService.activate(1L, LocalDate.of(2026, 2, 10)));

        verify(policyRepository).expireActivePolicies(LocalDate.of(2026, 2, 10));
        verify(policy, never()).activate();
    }

    @Test
    void activateShouldCallActivateWhenValid() {
        Policy policy = mock(Policy.class);
        when(policy.getStartDate()).thenReturn(LocalDate.of(2026, 3, 1));

        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));

        Policy result = policyService.activate(1L, LocalDate.of(2026, 2, 10));

        assertSame(policy, result);
        verify(policyRepository).expireActivePolicies(LocalDate.of(2026, 2, 10));
        verify(policy).activate();
    }

    @Test
    void cancelShouldThrowNotFoundWhenPolicyMissing() {
        when(policyRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> policyService.cancel(1L, "reason", LocalDate.of(2026, 2, 10)));
        verify(policyRepository).expireActivePolicies(LocalDate.of(2026, 2, 10));
    }

    @Test
    void cancelShouldRejectWhenReasonBlank() {
        Policy policy = mock(Policy.class);
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));

        assertThrows(ConflictException.class, () -> policyService.cancel(1L, "  ", LocalDate.of(2026, 2, 10)));

        verify(policyRepository).expireActivePolicies(LocalDate.of(2026, 2, 10));
        verify(policy, never()).cancel(anyString(), any());
    }

    @Test
    void cancelShouldCallCancelWhenValid() {
        Policy policy = mock(Policy.class);
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));

        Policy result = policyService.cancel(1L, "Client request", LocalDate.of(2026, 2, 10));

        assertSame(policy, result);
        verify(policyRepository).expireActivePolicies(LocalDate.of(2026, 2, 10));
        verify(policy).cancel("Client request", LocalDate.of(2026, 2, 10));
    }

    @Test
    void getShouldThrowNotFoundWhenPolicyMissing() {
        when(policyRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> policyService.get(1L));
        verify(policyRepository).expireActivePolicies(any(LocalDate.class));
    }

    @Test
    void getShouldReturnPolicyWhenExists() {
        Policy policy = mock(Policy.class);
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));

        Policy result = policyService.get(1L);

        assertSame(policy, result);
        verify(policyRepository).expireActivePolicies(any(LocalDate.class));
    }

    @Test
    void searchShouldDelegateToRepositoryWithSpecificationAndPageable() {
        var pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        var expectedPage = new org.springframework.data.domain.PageImpl<Policy>(java.util.List.of());

        when(policyRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), eq(pageable)))
                .thenReturn(expectedPage);

        var result = policyService.search(
                1L,
                2L,
                PolicyStatus.DRAFT,
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 12, 31),
                pageable
        );

        assertSame(expectedPage, result);
        verify(policyRepository).expireActivePolicies(any(LocalDate.class));
        verify(policyRepository).findAll(any(org.springframework.data.jpa.domain.Specification.class), eq(pageable));
    }
}
