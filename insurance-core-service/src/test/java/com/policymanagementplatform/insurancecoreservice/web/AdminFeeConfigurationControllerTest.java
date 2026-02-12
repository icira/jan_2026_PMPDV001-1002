package com.policymanagementplatform.insurancecoreservice.web;

import com.policymanagementplatform.insurancecoreservice.domain.FeeConfiguration;
import com.policymanagementplatform.insurancecoreservice.domain.FeeConfigurationType;
import com.policymanagementplatform.insurancecoreservice.service.FeeConfigurationAdminService;
import com.policymanagementplatform.insurancecoreservice.web.dto.CreateFeeRequest;
import com.policymanagementplatform.insurancecoreservice.web.dto.UpdateFeeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class AdminFeeConfigurationControllerTest {

    /*
    LANES ADDED:
    - Controller unit tests for AdminFeeConfigurationController.
    WHY:
    - Covers admin fee endpoints + verifies delegation.
    */

    private FeeConfigurationAdminService feeService;
    private AdminFeeConfigurationController controller;

    @BeforeEach
    void setUp() {
        feeService = mock(FeeConfigurationAdminService.class);
        controller = new AdminFeeConfigurationController(feeService);
    }

    @Test
    void listShouldDelegateToServiceAndReturnList() {
        FeeConfiguration f1 = mock(FeeConfiguration.class);
        FeeConfiguration f2 = mock(FeeConfiguration.class);
        List<FeeConfiguration> expected = List.of(f1, f2);
        when(feeService.list()).thenReturn(expected);

        List<FeeConfiguration> result = controller.list();

        assertSame(expected, result);
        verify(feeService).list();
        verifyNoMoreInteractions(feeService);
    }

    @Test
    void createShouldDelegateToService() {
        CreateFeeRequest req = new CreateFeeRequest(
                "Admin fee",
                FeeConfigurationType.ADMIN_FEE,
                2.0,
                LocalDate.of(2026, 1, 1),
                null,
                true
        );

        FeeConfiguration created = mock(FeeConfiguration.class);
        when(feeService.create(
                req.name(),
                req.type(),
                req.percentage(),
                req.effectiveFrom(),
                req.effectiveTo(),
                req.active()
        )).thenReturn(created);

        FeeConfiguration result = controller.create(req);

        assertSame(created, result);
        verify(feeService).create(
                req.name(),
                req.type(),
                req.percentage(),
                req.effectiveFrom(),
                req.effectiveTo(),
                req.active()
        );
        verifyNoMoreInteractions(feeService);
    }

    @Test
    void updateShouldDelegateToService() {
        UpdateFeeRequest req = new UpdateFeeRequest(
                3.5,
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 12, 31),
                false
        );

        FeeConfiguration updated = mock(FeeConfiguration.class);
        when(feeService.update(9L, req.percentage(), req.effectiveFrom(), req.effectiveTo(), req.active()))
                .thenReturn(updated);

        FeeConfiguration result = controller.update(9L, req);

        assertSame(updated, result);
        verify(feeService).update(9L, req.percentage(), req.effectiveFrom(), req.effectiveTo(), req.active());
        verifyNoMoreInteractions(feeService);
    }
}
