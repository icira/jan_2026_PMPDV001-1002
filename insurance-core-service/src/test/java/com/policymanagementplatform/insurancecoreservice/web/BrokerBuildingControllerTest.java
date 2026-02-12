package com.policymanagementplatform.insurancecoreservice.web;

import com.policymanagementplatform.insurancecoreservice.domain.Building;
import com.policymanagementplatform.insurancecoreservice.domain.BuildingType;
import com.policymanagementplatform.insurancecoreservice.service.BuildingService;
import com.policymanagementplatform.insurancecoreservice.web.dto.CreateBuildingRequest;
import com.policymanagementplatform.insurancecoreservice.web.dto.UpdateBuildingRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.*;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BrokerBuildingControllerTest {

    /*
    LANES ADDED:
    - Updated controller tests to include risk flags on create/update.
    WHY:
    - Part 2 request DTOs and service methods include earthquake/flood flags.
    */

    private BuildingService buildingService;
    private BrokerBuildingController controller;

    @BeforeEach
    void setUp() {
        buildingService = mock(BuildingService.class);
        controller = new BrokerBuildingController(buildingService);
    }

    @Test
    void listByClientShouldCreatePageRequestWithSortAndDelegateToService() {
        Long clientId = 1L;
        int page = 2;
        int size = 5;

        Pageable expectedPageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Building> expectedPage = new PageImpl<>(List.of(mock(Building.class)), expectedPageable, 1);

        when(buildingService.listByClient(eq(clientId), any(Pageable.class))).thenReturn(expectedPage);

        Page<Building> result = controller.listByClient(clientId, page, size);

        assertSame(expectedPage, result);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(buildingService).listByClient(eq(clientId), pageableCaptor.capture());

        Pageable actual = pageableCaptor.getValue();
        assertNotNull(actual);
        assertEquals(page, actual.getPageNumber());
        assertEquals(size, actual.getPageSize());

        Sort.Order order = actual.getSort().getOrderFor("id");
        assertNotNull(order, "Sort must contain order for 'id'");
        assertEquals(Sort.Direction.DESC, order.getDirection());

        verifyNoMoreInteractions(buildingService);
    }

    @Test
    void createShouldDelegateToServiceAndReturnCreatedResponseWithLocation() {
        Long clientId = 10L;

        CreateBuildingRequest req = new CreateBuildingRequest(
                99L,
                "Observatorului",
                "10A",
                2010,
                BuildingType.RESIDENTIAL,
                2,
                120.5,
                100_000.0,
                true,
                false
        );

        Building created = mock(Building.class);
        when(created.getId()).thenReturn(123L);

        when(buildingService.createForClient(
                eq(clientId),
                eq(req.cityId()),
                eq(req.street()),
                eq(req.number()),
                eq(req.constructionYear()),
                eq(req.type()),
                eq(req.floors()),
                eq(req.surfaceArea()),
                eq(req.insuredValue()),
                eq(req.earthquakeRiskZone()),
                eq(req.floodRiskZone())
        )).thenReturn(created);

        var response = controller.create(clientId, req);

        assertEquals(201, response.getStatusCode().value());
        assertEquals(URI.create("/api/brokers/buildings/123"), response.getHeaders().getLocation());
        assertSame(created, response.getBody());

        verify(buildingService).createForClient(
                clientId,
                req.cityId(),
                req.street(),
                req.number(),
                req.constructionYear(),
                req.type(),
                req.floors(),
                req.surfaceArea(),
                req.insuredValue(),
                req.earthquakeRiskZone(),
                req.floodRiskZone()
        );
        verifyNoMoreInteractions(buildingService);
    }

    @Test
    void updateShouldDelegateToServiceUsingRequestFieldsAndReturnBuilding() {
        Long buildingId = 7L;

        UpdateBuildingRequest req = new UpdateBuildingRequest(
                55L,
                "New Street",
                "99",
                2015,
                BuildingType.INDUSTRIAL,
                5,
                350.0,
                999_999.0,
                false,
                true
        );

        Building updated = mock(Building.class);

        when(buildingService.update(
                eq(buildingId),
                eq(req.cityId()),
                eq(req.street()),
                eq(req.number()),
                eq(req.constructionYear()),
                eq(req.type()),
                eq(req.floors()),
                eq(req.surfaceArea()),
                eq(req.insuredValue()),
                eq(req.earthquakeRiskZone()),
                eq(req.floodRiskZone())
        )).thenReturn(updated);

        Building result = controller.update(buildingId, req);

        assertSame(updated, result);

        verify(buildingService).update(
                buildingId,
                req.cityId(),
                req.street(),
                req.number(),
                req.constructionYear(),
                req.type(),
                req.floors(),
                req.surfaceArea(),
                req.insuredValue(),
                req.earthquakeRiskZone(),
                req.floodRiskZone()
        );
        verifyNoMoreInteractions(buildingService);
    }

    @Test
    void getShouldDelegateToServiceAndReturnBuilding() {
        Long buildingId = 42L;
        Building building = mock(Building.class);
        when(buildingService.get(buildingId)).thenReturn(building);

        Building result = controller.get(buildingId);

        assertSame(building, result);

        verify(buildingService).get(buildingId);
        verifyNoMoreInteractions(buildingService);
    }
}
