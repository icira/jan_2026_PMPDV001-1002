package com.policymanagementplatform.insurancecoreservice.service;

import com.policymanagementplatform.insurancecoreservice.domain.Building;
import com.policymanagementplatform.insurancecoreservice.domain.BuildingType;
import com.policymanagementplatform.insurancecoreservice.domain.City;
import com.policymanagementplatform.insurancecoreservice.domain.Client;
import com.policymanagementplatform.insurancecoreservice.exceptions.NotFoundException;
import com.policymanagementplatform.insurancecoreservice.repository.BuildingRepository;
import com.policymanagementplatform.insurancecoreservice.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuildingServiceTest {

    /*
    LANES ADDED:
    - Updated tests to include building risk flags (earthquakeRiskZone, floodRiskZone).
    WHY:
    - Part 2 premium calculation depends on risk flags; service signature changed accordingly.
    */

    @Mock
    private BuildingRepository buildingRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private GeographyService geographyService;

    private BuildingService buildingService;

    @BeforeEach
    void setUp() {
        buildingService = new BuildingService(buildingRepository, clientRepository, geographyService);
    }

    @Test
    void listByClientShouldDelegateToRepositoryWithPageable() {
        Long clientId = 1L;
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());

        Page<Building> expected = new PageImpl<>(List.of(mock(Building.class)), pageable, 1);

        // Service checks that client exists; stub it to return a client
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(mock(Client.class)));
        when(buildingRepository.findByOwner_Id(clientId, pageable)).thenReturn(expected);

        Page<Building> result = buildingService.listByClient(clientId, pageable);

        assertSame(expected, result);
        // Verify the existence check then delegation to repository with the pageable
        verify(clientRepository).findById(clientId);
        verify(buildingRepository).findByOwner_Id(clientId, pageable);
        verifyNoMoreInteractions(buildingRepository, clientRepository);
        verifyNoInteractions(geographyService);
    }

    @Test
    void getShouldReturnBuildingWhenExists() {
        Building building = mock(Building.class);
        when(buildingRepository.findById(10L)).thenReturn(Optional.of(building));

        Building result = buildingService.get(10L);

        assertSame(building, result);
        verify(buildingRepository).findById(10L);
        verifyNoMoreInteractions(buildingRepository);
        verifyNoInteractions(clientRepository, geographyService);
    }

    @Test
    void getShouldThrowNotFoundWhenMissing() {
        when(buildingRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> buildingService.get(10L));

        verify(buildingRepository).findById(10L);
        verifyNoMoreInteractions(buildingRepository);
        verifyNoInteractions(clientRepository, geographyService);
    }

    @Test
    void createForClientShouldCreateAndSaveBuildingWhenClientExists() {
        Long clientId = 1L;
        Long cityId = 2L;

        Client client = mock(Client.class);
        City city = mock(City.class);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(geographyService.getCity(cityId)).thenReturn(city);

        when(buildingRepository.save(any(Building.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Building result = buildingService.createForClient(
                clientId,
                cityId,
                "Street",
                "10",
                2000,
                BuildingType.RESIDENTIAL,
                2,
                120.5,
                100_000.0,
                true,
                false
        );

        assertNotNull(result);

        ArgumentCaptor<Building> buildingCaptor = ArgumentCaptor.forClass(Building.class);
        verify(buildingRepository).save(buildingCaptor.capture());

        Building saved = buildingCaptor.getValue();
        assertNotNull(saved);

        assertEquals("Street", saved.getStreet());
        assertEquals("10", saved.getNumber());
        assertEquals(2000, saved.getConstructionYear());
        assertEquals(BuildingType.RESIDENTIAL, saved.getType());
        assertEquals(2, saved.getFloors());
        assertEquals(120.5, saved.getSurfaceArea(), 0.0001);
        assertEquals(100_000.0, saved.getInsuredValue(), 0.0001);
        assertTrue(saved.isEarthquakeRiskZone());
        assertFalse(saved.isFloodRiskZone());

        verify(clientRepository).findById(clientId);
        verify(geographyService).getCity(cityId);
        verifyNoMoreInteractions(clientRepository, geographyService, buildingRepository);
    }

    @Test
    void createForClientShouldThrowNotFoundWhenClientMissing() {
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> buildingService.createForClient(
                1L,
                2L,
                "Street",
                "10",
                2000,
                BuildingType.RESIDENTIAL,
                2,
                120.5,
                100_000.0,
                false,
                false
        ));

        verify(clientRepository).findById(1L);
        verifyNoInteractions(geographyService);
        verifyNoInteractions(buildingRepository);
    }

    @Test
    void updateShouldUpdateExistingBuilding() {
        City city = mock(City.class);
        when(geographyService.getCity(2L)).thenReturn(city);

        Building building = mock(Building.class);
        when(buildingRepository.findById(10L)).thenReturn(Optional.of(building));

        Building updated = buildingService.update(
                10L,
                2L,
                "New Street",
                "99",
                2010,
                BuildingType.INDUSTRIAL,
                5,
                350.0,
                999_999.0,
                false,
                true
        );

        assertSame(building, updated);
        verify(buildingRepository).findById(10L);
        verify(geographyService).getCity(2L);
        verify(building).updateDetails(
                city,
                "New Street",
                "99",
                2010,
                BuildingType.INDUSTRIAL,
                5,
                350.0,
                999_999.0,
                false,
                true
        );
        verifyNoMoreInteractions(buildingRepository, geographyService);
        verifyNoInteractions(clientRepository);
    }

    @Test
    void updateShouldThrowNotFoundWhenBuildingMissing() {
        when(buildingRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> buildingService.update(
                10L,
                2L,
                "New Street",
                "99",
                2010,
                BuildingType.INDUSTRIAL,
                5,
                350.0,
                999_999.0,
                false,
                true
        ));

        verify(buildingRepository).findById(10L);
        verifyNoInteractions(clientRepository);
        verifyNoInteractions(geographyService);
        verifyNoMoreInteractions(buildingRepository);
    }
}
