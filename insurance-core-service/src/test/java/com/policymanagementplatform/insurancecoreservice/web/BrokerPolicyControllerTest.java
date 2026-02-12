package com.policymanagementplatform.insurancecoreservice.web;

import com.policymanagementplatform.insurancecoreservice.domain.Policy;
import com.policymanagementplatform.insurancecoreservice.domain.PolicyStatus;
import com.policymanagementplatform.insurancecoreservice.service.PolicyService;
import com.policymanagementplatform.insurancecoreservice.web.dto.CancelPolicyRequest;
import com.policymanagementplatform.insurancecoreservice.web.dto.CreatePolicyRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BrokerPolicyControllerTest {

    /*
    LANES ADDED:
    - Updated to match controller param rename: startDateTo -> endDateTo.
WHY:
    - Controller signature changed for correctness and clarity.
    */

    private PolicyService policyService;
    private BrokerPolicyController controller;

    @BeforeEach
    void setUp() {
        policyService = mock(PolicyService.class);
        controller = new BrokerPolicyController(policyService);
    }

    @Test
    void listShouldBuildPageableWithIdDescAndDelegateToService() {
        Pageable expected = PageRequest.of(1, 5, Sort.by("id").descending());
        Page<Policy> expectedPage = new PageImpl<>(List.of(mock(Policy.class)), expected, 1);

        when(policyService.search(eq(10L), eq(20L), eq(PolicyStatus.ACTIVE), any(), any(), any(Pageable.class)))
                .thenReturn(expectedPage);

        Page<Policy> result = controller.list(
                10L,
                20L,
                PolicyStatus.ACTIVE,
                LocalDate.of(2026, 3, 1),     // startDateFrom
                LocalDate.of(2026, 12, 31),   // LANES ADDED: endDateTo
                1,
                5
        );

        assertSame(expectedPage, result);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(policyService).search(eq(10L), eq(20L), eq(PolicyStatus.ACTIVE), any(), any(), pageableCaptor.capture());

        Pageable actual = pageableCaptor.getValue();
        assertEquals(1, actual.getPageNumber());
        assertEquals(5, actual.getPageSize());
        assertEquals(Sort.Direction.DESC, Objects.requireNonNull(actual.getSort().getOrderFor("id")).getDirection());

        verifyNoMoreInteractions(policyService);
    }

    @Test
    void createDraftShouldDelegateToServiceAndReturnCreatedResponseWithLocation() {
        CreatePolicyRequest req = new CreatePolicyRequest(
                1L,
                2L,
                3L,
                1000.0,
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2027, 3, 1),
                99L
        );

        Policy created = mock(Policy.class);
        when(created.getId()).thenReturn(123L);

        when(policyService.createDraft(
                req.clientId(),
                req.buildingId(),
                req.currencyId(),
                req.basePremium(),
                req.startDate(),
                req.endDate(),
                req.brokerId()
        )).thenReturn(created);

        var response = controller.createDraft(req);

        assertEquals(201, response.getStatusCode().value());
        assertEquals(URI.create("/api/brokers/policies/123"), response.getHeaders().getLocation());
        assertSame(created, response.getBody());

        verify(policyService).createDraft(
                req.clientId(),
                req.buildingId(),
                req.currencyId(),
                req.basePremium(),
                req.startDate(),
                req.endDate(),
                req.brokerId()
        );
        verifyNoMoreInteractions(policyService);
    }

    @Test
    void getShouldDelegateToService() {
        Policy policy = mock(Policy.class);
        when(policyService.get(7L)).thenReturn(policy);

        Policy result = controller.get(7L);

        assertSame(policy, result);
        verify(policyService).get(7L);
        verifyNoMoreInteractions(policyService);
    }

    @Test
    void activateShouldDelegateToService() {
        Policy policy = mock(Policy.class);
        when(policyService.activate(eq(7L), any(LocalDate.class))).thenReturn(policy);

        Policy result = controller.activate(7L);

        assertSame(policy, result);
        verify(policyService).activate(eq(7L), any(LocalDate.class));
        verifyNoMoreInteractions(policyService);
    }

    @Test
    void cancelShouldDelegateToService() {
        CancelPolicyRequest req = new CancelPolicyRequest("Client request");

        Policy policy = mock(Policy.class);
        when(policyService.cancel(eq(7L), eq("Client request"), any(LocalDate.class))).thenReturn(policy);

        Policy result = controller.cancel(7L, req);

        assertSame(policy, result);
        verify(policyService).cancel(eq(7L), eq("Client request"), any(LocalDate.class));
        verifyNoMoreInteractions(policyService);
    }
}
