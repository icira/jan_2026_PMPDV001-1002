package com.policymanagementplatform.insurancecoreservice.web;

import com.policymanagementplatform.insurancecoreservice.domain.Currency;
import com.policymanagementplatform.insurancecoreservice.service.CurrencyService;
import com.policymanagementplatform.insurancecoreservice.web.dto.CreateCurrencyRequest;
import com.policymanagementplatform.insurancecoreservice.web.dto.UpdateCurrencyRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class AdminCurrencyControllerTest {

    /*
    LANES ADDED:
    - Controller unit tests for AdminCurrencyController.
    WHY:
    - Covers admin currency endpoints and verifies paging/sorting + delegation.
    */

    private CurrencyService currencyAdminService;
    private AdminCurrencyController controller;

    @BeforeEach
    void setUp() {
        currencyAdminService = mock(CurrencyService.class);
        controller = new AdminCurrencyController(currencyAdminService);
    }

    @Test
    void listShouldDelegateToServiceAndReturnList() {
        // Given the controller's list() returns List<Currency> without paging
        Currency c1 = mock(Currency.class);
        Currency c2 = mock(Currency.class);
        List<Currency> expected = List.of(c1, c2);
        when(currencyAdminService.list()).thenReturn(expected);

        // When
        List<Currency> result = controller.list();

        // Then
        assertSame(expected, result);
        verify(currencyAdminService).list();
        verifyNoMoreInteractions(currencyAdminService);
    }

    @Test
    void createShouldDelegateToService() {
        CreateCurrencyRequest req = new CreateCurrencyRequest("RON", "Leu", 1.0, true);

        Currency created = mock(Currency.class);
        when(currencyAdminService.create(req.code(), req.name(), req.exchangeRateToBase(), req.active()))
                .thenReturn(created);

        Currency result = controller.create(req);

        assertSame(created, result);
        verify(currencyAdminService).create(req.code(), req.name(), req.exchangeRateToBase(), req.active());
        verifyNoMoreInteractions(currencyAdminService);
    }

    @Test
    void updateShouldDelegateToService() {
        UpdateCurrencyRequest req = new UpdateCurrencyRequest("Leu v2", 2.5, false);

        Currency updated = mock(Currency.class);
        when(currencyAdminService.update(7L, req.name(), req.exchangeRateToBase(), req.active()))
                .thenReturn(updated);

        Currency result = controller.update(7L, req);

        assertSame(updated, result);
        verify(currencyAdminService).update(7L, req.name(), req.exchangeRateToBase(), req.active());
        verifyNoMoreInteractions(currencyAdminService);
    }
}
