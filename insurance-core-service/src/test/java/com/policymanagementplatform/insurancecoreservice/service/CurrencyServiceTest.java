package com.policymanagementplatform.insurancecoreservice.service;

import com.policymanagementplatform.insurancecoreservice.domain.Currency;
import com.policymanagementplatform.insurancecoreservice.domain.PolicyStatus;
import com.policymanagementplatform.insurancecoreservice.exceptions.ConflictException;
import com.policymanagementplatform.insurancecoreservice.exceptions.NotFoundException;
import com.policymanagementplatform.insurancecoreservice.repository.CurrencyRepository;
import com.policymanagementplatform.insurancecoreservice.repository.PolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CurrencyServiceTest {

    /*
    LANES ADDED:
    - Unit tests for CurrencyAdminService.
    WHY:
    - Covers: create/get/list/update + constraint: cannot deactivate if active policies use currency.
    */

    private CurrencyRepository currencyRepository;
    private PolicyRepository policyRepository;
    private CurrencyService service;

    @BeforeEach
    void setUp() {
        currencyRepository = mock(CurrencyRepository.class);
        policyRepository = mock(PolicyRepository.class);
        service = new CurrencyService(currencyRepository, policyRepository);
    }

    @Test
    void createShouldSaveCurrency() {
        when(currencyRepository.findByCodeIgnoreCase("RON")).thenReturn(Optional.empty());
        when(currencyRepository.save(any(Currency.class))).thenAnswer(inv -> inv.getArgument(0));

        Currency created = service.create("RON", "Leu", 1.0, true);

        assertNotNull(created);
        assertEquals("RON", created.getCode());
        assertTrue(created.isActive());

        verify(currencyRepository).findByCodeIgnoreCase("RON");
        verify(currencyRepository).save(any(Currency.class));
        verifyNoMoreInteractions(currencyRepository);
        verifyNoInteractions(policyRepository);
    }

    @Test
    void getShouldThrowNotFoundWhenMissing() {
        when(currencyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.get(1L));

        verify(currencyRepository).findById(1L);
        verifyNoMoreInteractions(currencyRepository);
        verifyNoInteractions(policyRepository);
    }

    @Test
    void getShouldReturnWhenExists() {
        Currency currency = mock(Currency.class);
        when(currencyRepository.findById(1L)).thenReturn(Optional.of(currency));

        Currency result = service.get(1L);

        assertSame(currency, result);
        verify(currencyRepository).findById(1L);
        verifyNoMoreInteractions(currencyRepository);
        verifyNoInteractions(policyRepository);
    }

    @Test
    void updateShouldThrowNotFoundWhenMissing() {
        when(currencyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.update(1L, "Name", 2.0, true));

        verify(currencyRepository).findById(1L);
        verifyNoMoreInteractions(currencyRepository);
        verifyNoInteractions(policyRepository);
    }

    @Test
    void updateShouldRejectDeactivationWhenCurrencyUsedByActivePolicies() {
        Currency currency = spy(new Currency("RON", "Leu", 1.0, true));
        when(currencyRepository.findById(1L)).thenReturn(Optional.of(currency));
        // Service checks ACTIVE policies via existsByCurrency_IdAndStatus
        when(policyRepository.existsByCurrency_IdAndStatus(1L, PolicyStatus.ACTIVE)).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.update(1L, "Leu", 2.0, false));

        verify(currencyRepository).findById(1L);
        verify(policyRepository).existsByCurrency_IdAndStatus(1L, PolicyStatus.ACTIVE);
    }

    @Test
    void updateShouldAllowDeactivationWhenNoActivePolicyUsesCurrency() {
        Currency currency = spy(new Currency("RON", "Leu", 1.0, true));
        when(currencyRepository.findById(1L)).thenReturn(Optional.of(currency));
        // No ACTIVE policies should allow deactivation
        when(policyRepository.existsByCurrency_IdAndStatus(1L, PolicyStatus.ACTIVE)).thenReturn(false);

        Currency updated = service.update(1L, "Leu v2", 2.5, false);

        assertSame(currency, updated);
        assertEquals("Leu v2", updated.getName());
        assertEquals(2.5, updated.getExchangeRateToBase(), 0.0001);
        assertFalse(updated.isActive());

        verify(currencyRepository).findById(1L);
        verify(policyRepository).existsByCurrency_IdAndStatus(1L, PolicyStatus.ACTIVE);
    }

    @Test
    void listShouldDelegateToRepository() {
        Currency c1 = mock(Currency.class);
        Currency c2 = mock(Currency.class);
        List<Currency> expected = List.of(c1, c2);
        when(currencyRepository.findAll()).thenReturn(expected);

        List<Currency> result = service.list();

        assertSame(expected, result);
        verify(currencyRepository).findAll();
        verifyNoMoreInteractions(currencyRepository);
        verifyNoInteractions(policyRepository);
    }

}
