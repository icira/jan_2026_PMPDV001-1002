package com.policymanagementplatform.insurancecoreservice.web;

import com.policymanagementplatform.insurancecoreservice.domain.City;
import com.policymanagementplatform.insurancecoreservice.domain.Country;
import com.policymanagementplatform.insurancecoreservice.domain.County;
import com.policymanagementplatform.insurancecoreservice.service.GeographyService;
import com.policymanagementplatform.insurancecoreservice.web.dto.GeographyDtos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BrokerGeographyControllerTest {

    /*
    LANES ADDED:
    - Mock GeographyService dependency.
    WHY:
    - Unit test isolates controller logic and mapping from service implementation.
    */
    private GeographyService geographyService;

    /*
    LANES ADDED:
    - Controller under test.
    WHY:
    - We call controller methods directly (no Spring MVC needed).
    */
    private BrokerGeographyController controller;

    @BeforeEach
    void setUp() {
        geographyService = mock(GeographyService.class);
        controller = new BrokerGeographyController(geographyService);
    }

    // ------------------------
    // countries(...)
    // ------------------------

    @Test
    void countriesShouldCreatePageRequestSortByNameAscAndMapToDto() {
        // Arrange
        int page = 1;
        int size = 2;

        Country c1 = mock(Country.class);
        when(c1.getId()).thenReturn(10L);
        when(c1.getName()).thenReturn("Romania");

        Country c2 = mock(Country.class);
        when(c2.getId()).thenReturn(11L);
        when(c2.getName()).thenReturn("Bulgaria");

        Pageable expectedPageable = PageRequest.of(page, size, Sort.by("name").ascending());

        // IMPORTANT:
        // totalElements can be > content size (e.g. 4 total countries, but only 2 on this page).
        long totalElements = 4L;
        Page<Country> servicePage = new PageImpl<>(List.of(c1, c2), expectedPageable, totalElements);

        when(geographyService.listCountries(any(Pageable.class))).thenReturn(servicePage);

        // Act
        Page<GeographyDtos.CountryResponse> result = controller.countries(page, size);

        // Assert pageable and sorting
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(geographyService).listCountries(pageableCaptor.capture());

        Pageable actual = pageableCaptor.getValue();
        assertEquals(page, actual.getPageNumber());
        assertEquals(size, actual.getPageSize());

        Sort.Order order = actual.getSort().getOrderFor("name");
        assertNotNull(order, "Sort must contain order for 'name'");
        assertEquals(Sort.Direction.ASC, order.getDirection());

        // Assert paging metadata:
        // - totalElements is preserved by Page.map(...)
        // - content size is the number of items on the current page
        assertEquals(servicePage.getTotalElements(), result.getTotalElements());
        assertEquals(2, result.getContent().size());

        // Assert mapping to DTO
        assertEquals(10L, result.getContent().get(0).id());
        assertEquals("Romania", result.getContent().get(0).name());

        assertEquals(11L, result.getContent().get(1).id());
        assertEquals("Bulgaria", result.getContent().get(1).name());

        verifyNoMoreInteractions(geographyService);
    }


    // ------------------------
    // counties(...)
    // ------------------------

    @Test
    void countiesShouldCreatePageRequestSortByNameAscAndMapToDto() {
        // Arrange
        Long countryId = 1L;
        int page = 0;
        int size = 3;

        Country country = mock(Country.class);
        when(country.getId()).thenReturn(countryId);

        County ct1 = mock(County.class);
        when(ct1.getId()).thenReturn(100L);
        when(ct1.getName()).thenReturn("Cluj");
        when(ct1.getCountry()).thenReturn(country);

        County ct2 = mock(County.class);
        when(ct2.getId()).thenReturn(101L);
        when(ct2.getName()).thenReturn("Bihor");
        when(ct2.getCountry()).thenReturn(country);

        Pageable expectedPageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<County> servicePage = new PageImpl<>(List.of(ct1, ct2), expectedPageable, 2);

        when(geographyService.listCounties(eq(countryId), any(Pageable.class))).thenReturn(servicePage);

        // Act
        Page<GeographyDtos.CountyResponse> result = controller.counties(countryId, page, size);

        // Assert pageable
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(geographyService).listCounties(eq(countryId), pageableCaptor.capture());

        Pageable actual = pageableCaptor.getValue();
        assertEquals(page, actual.getPageNumber());
        assertEquals(size, actual.getPageSize());

        Sort.Order order = actual.getSort().getOrderFor("name");
        assertNotNull(order, "Sort must contain order for 'name'");
        assertEquals(Sort.Direction.ASC, order.getDirection());

        // Assert mapping
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());

        GeographyDtos.CountyResponse r1 = result.getContent().getFirst();
        assertEquals(100L, r1.id());
        assertEquals(countryId, r1.countryId());
        assertEquals("Cluj", r1.name());

        GeographyDtos.CountyResponse r2 = result.getContent().get(1);
        assertEquals(101L, r2.id());
        assertEquals(countryId, r2.countryId());
        assertEquals("Bihor", r2.name());

        verifyNoMoreInteractions(geographyService);
    }

    // ------------------------
    // cities(...)
    // ------------------------

    @Test
    void citiesShouldCreatePageRequestSortByNameAscAndMapToDto() {
        // Arrange
        Long countyId = 10L;
        int page = 2;
        int size = 5;

        County county = mock(County.class);
        when(county.getId()).thenReturn(countyId);

        City ci1 = mock(City.class);
        when(ci1.getId()).thenReturn(200L);
        when(ci1.getName()).thenReturn("Cluj-Napoca");
        when(ci1.getCode()).thenReturn("CJ-NAP");
        when(ci1.getCounty()).thenReturn(county);

        City ci2 = mock(City.class);
        when(ci2.getId()).thenReturn(201L);
        when(ci2.getName()).thenReturn("Turda");
        when(ci2.getCode()).thenReturn("CJ-TUR");
        when(ci2.getCounty()).thenReturn(county);

        Pageable expectedPageable = PageRequest.of(page, size, Sort.by("name").ascending());

        // IMPORTANT:
        // totalElements can be > content size (e.g. 12 total cities, but only 2 in this page).
        long totalElements = 12L;
        Page<City> servicePage = new PageImpl<>(List.of(ci1, ci2), expectedPageable, totalElements);

        when(geographyService.listCities(eq(countyId), any(Pageable.class))).thenReturn(servicePage);

        // Act
        Page<GeographyDtos.CityResponse> result = controller.cities(countyId, page, size);

        // Assert pageable
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(geographyService).listCities(eq(countyId), pageableCaptor.capture());

        Pageable actual = pageableCaptor.getValue();
        assertEquals(page, actual.getPageNumber());
        assertEquals(size, actual.getPageSize());

        Sort.Order order = actual.getSort().getOrderFor("name");
        assertNotNull(order, "Sort must contain order for 'name'");
        assertEquals(Sort.Direction.ASC, order.getDirection());

        // Assert mapping:
        // - totalElements must match the source page
        // - content size must match current page content
        assertEquals(servicePage.getTotalElements(), result.getTotalElements());
        assertEquals(2, result.getContent().size());

        GeographyDtos.CityResponse r1 = result.getContent().getFirst();
        assertEquals(200L, r1.id());
        assertEquals(countyId, r1.countyId());
        assertEquals("Cluj-Napoca", r1.name());
        assertEquals("CJ-NAP", r1.code());

        GeographyDtos.CityResponse r2 = result.getContent().get(1);
        assertEquals(201L, r2.id());
        assertEquals(countyId, r2.countyId());
        assertEquals("Turda", r2.name());
        assertEquals("CJ-TUR", r2.code());

        verifyNoMoreInteractions(geographyService);
    }

}

