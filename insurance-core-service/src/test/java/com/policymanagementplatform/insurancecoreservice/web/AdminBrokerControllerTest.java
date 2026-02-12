package com.policymanagementplatform.insurancecoreservice.web;

import com.policymanagementplatform.insurancecoreservice.domain.Broker;
import com.policymanagementplatform.insurancecoreservice.domain.BrokerStatus;
import com.policymanagementplatform.insurancecoreservice.service.BrokerService;
import com.policymanagementplatform.insurancecoreservice.web.dto.CreateBrokerRequest;
import com.policymanagementplatform.insurancecoreservice.web.dto.UpdateBrokerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminBrokerControllerTest {

    /*
    LANES ADDED:
    - Controller unit tests for AdminBrokerController.
    WHY:
    - Covers admin broker endpoints: list/get/create/update/status.
    - Verifies paging + sorting + delegation to service.
    */

    private BrokerService brokerAdminService;
    private AdminBrokerController controller;

    @BeforeEach
    void setUp() {
        brokerAdminService = mock(BrokerService.class);
        controller = new AdminBrokerController(brokerAdminService);
    }

    @Test
    void listShouldBuildPageableWithIdDescAndDelegateToService() {
        Pageable expected = PageRequest.of(0, 10, Sort.by("id").descending());
        Page<Broker> expectedPage = new PageImpl<>(List.of(mock(Broker.class)), expected, 1);

        when(brokerAdminService.list(any(Pageable.class))).thenReturn(expectedPage);

        Page<Broker> result = controller.list(0, 10);

        assertSame(expectedPage, result);
        verify(brokerAdminService).list(argThat(p ->
                p.getPageNumber() == 0
                        && p.getPageSize() == 10
                        && p.getSort().getOrderFor("id") != null
                        && Objects.requireNonNull(p.getSort().getOrderFor("id")).getDirection() == Sort.Direction.DESC
        ));
        verifyNoMoreInteractions(brokerAdminService);
    }

    @Test
    void getShouldDelegateToService() {
        Broker broker = mock(Broker.class);
        when(brokerAdminService.get(7L)).thenReturn(broker);

        Broker result = controller.get(7L);

        assertSame(broker, result);
        verify(brokerAdminService).get(7L);
        verifyNoMoreInteractions(brokerAdminService);
    }

    @Test
    void createShouldDelegateToService() {
        CreateBrokerRequest req = new CreateBrokerRequest(
                "BR-1",
                "Broker One",
                "b1@demo.com",
                "0712345678",
                5.0,
                BrokerStatus.ACTIVE
        );

        Broker created = mock(Broker.class);
        when(brokerAdminService.create(
                req.brokerCode(),
                req.name(),
                req.email(),
                req.phone(),
                req.commissionPercentage(),
                req.initialStatus()
        )).thenReturn(created);

        Broker result = controller.create(req);

        assertSame(created, result);
        verify(brokerAdminService).create(
                req.brokerCode(),
                req.name(),
                req.email(),
                req.phone(),
                req.commissionPercentage(),
                req.initialStatus()
        );
        verifyNoMoreInteractions(brokerAdminService);
    }

    @Test
    void updateShouldDelegateToService() {
        UpdateBrokerRequest req = new UpdateBrokerRequest(
                "Broker Updated",
                "updated@demo.com",
                "0799999999",
                7.5
        );

        Broker updated = mock(Broker.class);
        when(brokerAdminService.update(
                10L,
                req.name(),
                req.email(),
                req.phone(),
                req.commissionPercentage()
        )).thenReturn(updated);

        Broker result = controller.update(10L, req);

        assertSame(updated, result);
        verify(brokerAdminService).update(
                10L,
                req.name(),
                req.email(),
                req.phone(),
                req.commissionPercentage()
        );
        verifyNoMoreInteractions(brokerAdminService);
    }

    @Test
    void changeStatusShouldDelegateToService() {
        Broker updated = mock(Broker.class);
        when(brokerAdminService.changeStatus(10L, BrokerStatus.INACTIVE)).thenReturn(updated);

        Broker result = controller.deactivate(10L);

        assertSame(updated, result);
        verify(brokerAdminService).changeStatus(10L, BrokerStatus.INACTIVE);
        verifyNoMoreInteractions(brokerAdminService);
    }
}
