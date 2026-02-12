package com.policymanagementplatform.insurancecoreservice.web;

import com.policymanagementplatform.insurancecoreservice.domain.City;
import com.policymanagementplatform.insurancecoreservice.domain.Country;
import com.policymanagementplatform.insurancecoreservice.domain.County;
import com.policymanagementplatform.insurancecoreservice.service.GeographyService;
import com.policymanagementplatform.insurancecoreservice.web.dto.GeographyDtos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminGeographyControllerTest {

    /*
    LANES ADDED:
    - Mock GeographyService dependency.
    WHY:
    - Unit test isolates controller logic from service layer.
    */
    private GeographyService geographyService;

    /*
    LANES ADDED:
    - Controller under test.
    WHY:
    - We call controller methods directly (fast + deterministic).
    */
    private AdminGeographyController controller;

    @BeforeEach
    void setUp() {
        geographyService = mock(GeographyService.class);
        controller = new AdminGeographyController(geographyService);
    }

    // ------------------------
    // createCountry(...)
    // ------------------------

    @Test
    void createCountryShouldThrowWhenRequestIsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.createCountry(null)
        );
        assertEquals("name is required", ex.getMessage());
        verifyNoInteractions(geographyService);
    }

    @Test
    void createCountryShouldThrowWhenNameIsNull() {
        GeographyDtos.CreateCountryRequest req = new GeographyDtos.CreateCountryRequest(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.createCountry(req)
        );
        assertEquals("name is required", ex.getMessage());
        verifyNoInteractions(geographyService);
    }

    @Test
    void createCountryShouldThrowWhenNameIsBlank() {
        GeographyDtos.CreateCountryRequest req = new GeographyDtos.CreateCountryRequest("   ");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.createCountry(req)
        );
        assertEquals("name is required", ex.getMessage());
        verifyNoInteractions(geographyService);
    }

    @Test
    void createCountryShouldCreateCountryWhenValidAndTrimName() {
        // Arrange
        Country saved = mock(Country.class);
        when(saved.getId()).thenReturn(10L);
        when(saved.getName()).thenReturn("Romania");

        when(geographyService.createCountry("Romania")).thenReturn(saved);

        GeographyDtos.CreateCountryRequest req = new GeographyDtos.CreateCountryRequest("  Romania  ");

        // Act
        ResponseEntity<GeographyDtos.CountryResponse> response = controller.createCountry(req);

        // Assert: status and location
        assertEquals(201, response.getStatusCode().value());
        assertEquals(URI.create("/api/admin/geography/countries/10"), response.getHeaders().getLocation());

        // Assert: body mapping
        assertNotNull(response.getBody());
        assertEquals(10L, response.getBody().id());
        assertEquals("Romania", response.getBody().name());

        // Verify trim behavior
        verify(geographyService).createCountry("Romania");
        verifyNoMoreInteractions(geographyService);
    }

    // ------------------------
    // createCounty(...)
    // ------------------------

    @Test
    void createCountyShouldThrowWhenRequestIsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.createCounty(null)
        );
        assertEquals("countryId and name are required", ex.getMessage());
        verifyNoInteractions(geographyService);
    }

    @Test
    void createCountyShouldThrowWhenCountryIdIsNull() {
        GeographyDtos.CreateCountyRequest req = new GeographyDtos.CreateCountyRequest(null, "Cluj");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.createCounty(req)
        );
        assertEquals("countryId and name are required", ex.getMessage());
        verifyNoInteractions(geographyService);
    }

    @Test
    void createCountyShouldThrowWhenNameIsNull() {
        GeographyDtos.CreateCountyRequest req = new GeographyDtos.CreateCountyRequest(1L, null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.createCounty(req)
        );
        assertEquals("countryId and name are required", ex.getMessage());
        verifyNoInteractions(geographyService);
    }

    @Test
    void createCountyShouldThrowWhenNameIsBlank() {
        GeographyDtos.CreateCountyRequest req = new GeographyDtos.CreateCountyRequest(1L, "   ");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.createCounty(req)
        );
        assertEquals("countryId and name are required", ex.getMessage());
        verifyNoInteractions(geographyService);
    }

    @Test
    void createCountyShouldCreateCountyWhenValidAndTrimName() {
        // Arrange
        Country country = mock(Country.class);
        when(country.getId()).thenReturn(1L);

        County saved = mock(County.class);
        when(saved.getId()).thenReturn(20L);
        when(saved.getName()).thenReturn("Cluj");
        when(saved.getCountry()).thenReturn(country);

        when(geographyService.createCounty(1L, "Cluj")).thenReturn(saved);

        GeographyDtos.CreateCountyRequest req = new GeographyDtos.CreateCountyRequest(1L, "  Cluj  ");

        // Act
        ResponseEntity<GeographyDtos.CountyResponse> response = controller.createCounty(req);

        // Assert: status and location
        assertEquals(201, response.getStatusCode().value());
        assertEquals(URI.create("/api/admin/geography/counties/20"), response.getHeaders().getLocation());

        // Assert: body mapping
        assertNotNull(response.getBody());
        assertEquals(20L, response.getBody().id());
        assertEquals(1L, response.getBody().countryId());
        assertEquals("Cluj", response.getBody().name());

        // Verify trim behavior
        verify(geographyService).createCounty(1L, "Cluj");
        verifyNoMoreInteractions(geographyService);
    }

    // ------------------------
    // createCity(...)
    // ------------------------

    @Test
    void createCityShouldThrowWhenRequestIsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.createCity(null)
        );
        assertEquals("countyId, name and code are required", ex.getMessage());
        verifyNoInteractions(geographyService);
    }

    @Test
    void createCityShouldThrowWhenCountyIdIsNull() {
        GeographyDtos.CreateCityRequest req = new GeographyDtos.CreateCityRequest(null, "Cluj-Napoca", "CJ");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.createCity(req)
        );
        assertEquals("countyId, name and code are required", ex.getMessage());
        verifyNoInteractions(geographyService);
    }

    @Test
    void createCityShouldThrowWhenNameIsNull() {
        GeographyDtos.CreateCityRequest req = new GeographyDtos.CreateCityRequest(10L, null, "CJ");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.createCity(req)
        );
        assertEquals("countyId, name and code are required", ex.getMessage());
        verifyNoInteractions(geographyService);
    }

    @Test
    void createCityShouldThrowWhenNameIsBlank() {
        GeographyDtos.CreateCityRequest req = new GeographyDtos.CreateCityRequest(10L, "   ", "CJ");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.createCity(req)
        );
        assertEquals("countyId, name and code are required", ex.getMessage());
        verifyNoInteractions(geographyService);
    }

    @Test
    void createCityShouldThrowWhenCodeIsNull() {
        GeographyDtos.CreateCityRequest req = new GeographyDtos.CreateCityRequest(10L, "Cluj-Napoca", null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.createCity(req)
        );
        assertEquals("countyId, name and code are required", ex.getMessage());
        verifyNoInteractions(geographyService);
    }

    @Test
    void createCityShouldThrowWhenCodeIsBlank() {
        GeographyDtos.CreateCityRequest req = new GeographyDtos.CreateCityRequest(10L, "Cluj-Napoca", "   ");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.createCity(req)
        );
        assertEquals("countyId, name and code are required", ex.getMessage());
        verifyNoInteractions(geographyService);
    }

    @Test
    void createCityShouldCreateCityWhenValidAndTrimNameAndCode() {
        // Arrange
        County county = mock(County.class);
        when(county.getId()).thenReturn(10L);

        City saved = mock(City.class);
        when(saved.getId()).thenReturn(30L);
        when(saved.getName()).thenReturn("Cluj-Napoca");
        when(saved.getCode()).thenReturn("CJ-NAP");
        when(saved.getCounty()).thenReturn(county);

        when(geographyService.createCity(10L, "Cluj-Napoca", "CJ-NAP")).thenReturn(saved);

        GeographyDtos.CreateCityRequest req = new GeographyDtos.CreateCityRequest(10L, "  Cluj-Napoca  ", "  CJ-NAP  ");

        // Act
        ResponseEntity<GeographyDtos.CityResponse> response = controller.createCity(req);

        // Assert: status and location
        assertEquals(201, response.getStatusCode().value());
        assertEquals(URI.create("/api/admin/geography/cities/30"), response.getHeaders().getLocation());

        // Assert: body mapping
        assertNotNull(response.getBody());
        assertEquals(30L, response.getBody().id());
        assertEquals(10L, response.getBody().countyId());
        assertEquals("Cluj-Napoca", response.getBody().name());
        assertEquals("CJ-NAP", response.getBody().code());

        // Verify trim behavior
        verify(geographyService).createCity(10L, "Cluj-Napoca", "CJ-NAP");
        verifyNoMoreInteractions(geographyService);
    }
}

