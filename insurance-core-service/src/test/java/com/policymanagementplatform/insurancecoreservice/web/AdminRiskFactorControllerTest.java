package com.policymanagementplatform.insurancecoreservice.web;

import com.policymanagementplatform.insurancecoreservice.domain.RiskFactorConfiguration;
import com.policymanagementplatform.insurancecoreservice.domain.RiskFactorLevel;
import com.policymanagementplatform.insurancecoreservice.service.RiskFactorAdminService;
import com.policymanagementplatform.insurancecoreservice.web.dto.CreateRiskFactorRequest;
import com.policymanagementplatform.insurancecoreservice.web.dto.UpdateRiskFactorRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class AdminRiskFactorControllerTest {

    /*
    LANES ADDED:
    - Controller unit tests for AdminRiskFactorController.
    WHY:
    - Covers admin risk factor endpoints and validates delegation.
    */

    private RiskFactorAdminService riskService;
    private AdminRiskFactorController controller;

    @BeforeEach
    void setUp() {
        riskService = mock(RiskFactorAdminService.class);
        controller = new AdminRiskFactorController(riskService);
    }

    @Test
    void listShouldDelegateToServiceAndReturnList() {
        RiskFactorConfiguration r1 = mock(RiskFactorConfiguration.class);
        RiskFactorConfiguration r2 = mock(RiskFactorConfiguration.class);
        List<RiskFactorConfiguration> expected = List.of(r1, r2);
        when(riskService.list()).thenReturn(expected);

        List<RiskFactorConfiguration> result = controller.list();

        assertSame(expected, result);
        verify(riskService).list();
        verifyNoMoreInteractions(riskService);
    }

    @Test
    void createShouldDelegateToService() {
        CreateRiskFactorRequest req = new CreateRiskFactorRequest(RiskFactorLevel.CITY, 10L, 3.0, true);

        RiskFactorConfiguration created = mock(RiskFactorConfiguration.class);
        when(riskService.create(req.level(), req.referenceId(), req.adjustmentPercentage(), req.active()))
                .thenReturn(created);

        RiskFactorConfiguration result = controller.create(req);

        assertSame(created, result);
        verify(riskService).create(req.level(), req.referenceId(), req.adjustmentPercentage(), req.active());
        verifyNoMoreInteractions(riskService);
    }

    @Test
    void updateShouldDelegateToService() {
        UpdateRiskFactorRequest req = new UpdateRiskFactorRequest(5.5, false);

        RiskFactorConfiguration updated = mock(RiskFactorConfiguration.class);
        when(riskService.update(7L, req.adjustmentPercentage(), req.active()))
                .thenReturn(updated);

        RiskFactorConfiguration result = controller.update(7L, req);

        assertSame(updated, result);
        verify(riskService).update(7L, req.adjustmentPercentage(), req.active());
        verifyNoMoreInteractions(riskService);
    }
}
