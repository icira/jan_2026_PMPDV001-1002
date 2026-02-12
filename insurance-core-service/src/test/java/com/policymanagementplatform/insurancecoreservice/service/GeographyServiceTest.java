package com.policymanagementplatform.insurancecoreservice.service;

import com.policymanagementplatform.insurancecoreservice.domain.City;
import com.policymanagementplatform.insurancecoreservice.domain.Country;
import com.policymanagementplatform.insurancecoreservice.domain.County;
import com.policymanagementplatform.insurancecoreservice.exceptions.ConflictException;
import com.policymanagementplatform.insurancecoreservice.exceptions.NotFoundException;
import com.policymanagementplatform.insurancecoreservice.repository.CityRepository;
import com.policymanagementplatform.insurancecoreservice.repository.CountryRepository;
import com.policymanagementplatform.insurancecoreservice.repository.CountyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeographyServiceTest {

    /*
    LANES ADDED:
    - Mock the repositories used by GeographyService.
    WHY:
    - Unit tests should isolate service logic from persistence.
    */
    @Mock private CountryRepository countryRepository;
    @Mock private CountyRepository countyRepository;
    @Mock private CityRepository cityRepository;

    /*
    LANES ADDED:
    - Service under test (SUT).
    WHY:
    - This is the class we are verifying.
    */
    private GeographyService geographyService;

    @BeforeEach
    void setUp() {
        geographyService = new GeographyService(countryRepository, countyRepository, cityRepository);
    }

    // ----------------------
    // listCountries(...)
    // ----------------------

    @Test
    void listCountriesShouldReturnCountriesPage() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        Page<Country> expected = new PageImpl<>(List.of(mock(Country.class)), pageable, 1);

        when(countryRepository.findAll(pageable)).thenReturn(expected);

        Page<Country> result = geographyService.listCountries(pageable);

        assertSame(expected, result);

        verify(countryRepository).findAll(pageable);
        verifyNoMoreInteractions(countryRepository);
        verifyNoInteractions(countyRepository, cityRepository);
    }

    // ----------------------
    // listCounties(...)
    // ----------------------

    @Test
    void listCountiesShouldReturnCountiesPageWhenCountryExists() {
        Long countryId = 1L;
        Pageable pageable = PageRequest.of(0, 20);

        when(countryRepository.findById(countryId)).thenReturn(Optional.of(mock(Country.class)));

        Page<County> expected = new PageImpl<>(List.of(mock(County.class), mock(County.class)), pageable, 2);
        when(countyRepository.findByCountry_Id(countryId, pageable)).thenReturn(expected);

        Page<County> result = geographyService.listCounties(countryId, pageable);

        assertSame(expected, result);

        verify(countryRepository).findById(countryId);
        verify(countyRepository).findByCountry_Id(countryId, pageable);
        verifyNoMoreInteractions(countryRepository, countyRepository);
        verifyNoInteractions(cityRepository);
    }

    @Test
    void listCountiesShouldThrowNotFoundWhenCountryMissing() {
        Long countryId = 404L;
        Pageable pageable = PageRequest.of(0, 20);

        when(countryRepository.findById(countryId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () ->
                geographyService.listCounties(countryId, pageable)
        );

        assertEquals("Country not found", ex.getMessage());

        verify(countryRepository).findById(countryId);
        verifyNoInteractions(countyRepository);
        verifyNoInteractions(cityRepository);
        verifyNoMoreInteractions(countryRepository);
    }

    // ----------------------
    // listCities(...)
    // ----------------------

    @Test
    void listCitiesShouldReturnCitiesPageWhenCountyExists() {
        Long countyId = 10L;
        Pageable pageable = PageRequest.of(0, 50);

        when(countyRepository.findById(countyId)).thenReturn(Optional.of(mock(County.class)));

        Page<City> expected = new PageImpl<>(List.of(mock(City.class)), pageable, 1);
        when(cityRepository.findByCounty_Id(countyId, pageable)).thenReturn(expected);

        Page<City> result = geographyService.listCities(countyId, pageable);

        assertSame(expected, result);

        verify(countyRepository).findById(countyId);
        verify(cityRepository).findByCounty_Id(countyId, pageable);
        verifyNoMoreInteractions(countyRepository, cityRepository);
        verifyNoInteractions(countryRepository);
    }

    @Test
    void listCitiesShouldThrowNotFoundWhenCountyMissing() {
        Long countyId = 404L;
        Pageable pageable = PageRequest.of(0, 50);

        when(countyRepository.findById(countyId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () ->
                geographyService.listCities(countyId, pageable)
        );

        assertEquals("County not found", ex.getMessage());

        verify(countyRepository).findById(countyId);
        verifyNoInteractions(cityRepository);
        verifyNoInteractions(countryRepository);
        verifyNoMoreInteractions(countyRepository);
    }

    // ----------------------
    // getCity(...)
    // ----------------------

    @Test
    void getCityShouldReturnCityWhenExists() {
        Long cityId = 1L;
        City city = mock(City.class);

        when(cityRepository.findById(cityId)).thenReturn(Optional.of(city));

        City result = geographyService.getCity(cityId);

        assertSame(city, result);

        verify(cityRepository).findById(cityId);
        verifyNoMoreInteractions(cityRepository);
        verifyNoInteractions(countryRepository, countyRepository);
    }

    @Test
    void getCityShouldThrowNotFoundWhenCityMissing() {
        Long cityId = 404L;
        when(cityRepository.findById(cityId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () ->
                geographyService.getCity(cityId)
        );

        assertEquals("City not found", ex.getMessage());

        verify(cityRepository).findById(cityId);
        verifyNoMoreInteractions(cityRepository);
        verifyNoInteractions(countryRepository, countyRepository);
    }

    // ----------------------
    // createCountry(...)
    // ----------------------

    @Test
    void createCountryShouldSaveWhenNameIsUnique() {
        when(countryRepository.findByNameIgnoreCase("Romania")).thenReturn(Optional.empty());

        Country saved = mock(Country.class);
        when(countryRepository.save(any(Country.class))).thenReturn(saved);

        Country result = geographyService.createCountry("Romania");

        assertSame(saved, result);

        verify(countryRepository).findByNameIgnoreCase("Romania");

        ArgumentCaptor<Country> captor = ArgumentCaptor.forClass(Country.class);
        verify(countryRepository).save(captor.capture());
        assertNotNull(captor.getValue(), "Country passed to save() must not be null");

        verifyNoMoreInteractions(countryRepository);
        verifyNoInteractions(countyRepository, cityRepository);
    }

    @Test
    void createCountryShouldThrowConflictWhenNameAlreadyExists() {
        when(countryRepository.findByNameIgnoreCase("Romania")).thenReturn(Optional.of(mock(Country.class)));

        ConflictException ex = assertThrows(ConflictException.class, () ->
                geographyService.createCountry("Romania")
        );

        assertEquals("Country name already exists", ex.getMessage());

        verify(countryRepository).findByNameIgnoreCase("Romania");
        verify(countryRepository, never()).save(any());
        verifyNoMoreInteractions(countryRepository);
        verifyNoInteractions(countyRepository, cityRepository);
    }

    // ----------------------
    // createCounty(...)
    // ----------------------

    @Test
    void createCountyShouldThrowNotFoundWhenCountryMissing() {
        when(countryRepository.findById(404L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () ->
                geographyService.createCounty(404L, "Cluj")
        );

        assertEquals("Country not found", ex.getMessage());

        verify(countryRepository).findById(404L);
        verifyNoMoreInteractions(countryRepository);
        verifyNoInteractions(countyRepository, cityRepository);
    }

    @Test
    void createCountyShouldThrowConflictWhenCountyNameExistsInCountry() {
        Long countryId = 1L;
        when(countryRepository.findById(countryId)).thenReturn(Optional.of(mock(Country.class)));
        when(countyRepository.findByCountry_IdAndNameIgnoreCase(countryId, "Cluj"))
                .thenReturn(Optional.of(mock(County.class)));

        ConflictException ex = assertThrows(ConflictException.class, () ->
                geographyService.createCounty(countryId, "Cluj")
        );

        assertEquals("County name already exists for this country", ex.getMessage());

        verify(countryRepository).findById(countryId);
        verify(countyRepository).findByCountry_IdAndNameIgnoreCase(countryId, "Cluj");
        verify(countyRepository, never()).save(any());
        verifyNoInteractions(cityRepository);
        verifyNoMoreInteractions(countryRepository, countyRepository);
    }

    @Test
    void createCountyShouldSaveWhenCountyNameIsUniqueInCountry() {
        Long countryId = 1L;
        Country country = mock(Country.class);
        when(countryRepository.findById(countryId)).thenReturn(Optional.of(country));
        when(countyRepository.findByCountry_IdAndNameIgnoreCase(countryId, "Cluj")).thenReturn(Optional.empty());

        County saved = mock(County.class);
        when(countyRepository.save(any(County.class))).thenReturn(saved);

        County result = geographyService.createCounty(countryId, "Cluj");

        assertSame(saved, result);

        verify(countryRepository).findById(countryId);
        verify(countyRepository).findByCountry_IdAndNameIgnoreCase(countryId, "Cluj");

        ArgumentCaptor<County> captor = ArgumentCaptor.forClass(County.class);
        verify(countyRepository).save(captor.capture());
        assertNotNull(captor.getValue(), "County passed to save() must not be null");

        verifyNoInteractions(cityRepository);
        verifyNoMoreInteractions(countryRepository, countyRepository);
    }

    // ----------------------
    // createCity(...)
    // ----------------------

    @Test
    void createCityShouldThrowNotFoundWhenCountyMissing() {
        when(countyRepository.findById(404L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () ->
                geographyService.createCity(404L, "Cluj-Napoca", "CJ-NAP")
        );

        assertEquals("County not found", ex.getMessage());

        verify(countyRepository).findById(404L);
        verifyNoMoreInteractions(countyRepository);
        verifyNoInteractions(countryRepository, cityRepository);
    }

    @Test
    void createCityShouldThrowConflictWhenCityCodeExistsInCounty() {
        Long countyId = 10L;
        when(countyRepository.findById(countyId)).thenReturn(Optional.of(mock(County.class)));
        when(cityRepository.findByCounty_IdAndCodeIgnoreCase(countyId, "CJ-NAP"))
                .thenReturn(Optional.of(mock(City.class)));

        ConflictException ex = assertThrows(ConflictException.class, () ->
                geographyService.createCity(countyId, "Cluj-Napoca", "CJ-NAP")
        );

        assertEquals("City code already exists for this county", ex.getMessage());

        verify(countyRepository).findById(countyId);
        verify(cityRepository).findByCounty_IdAndCodeIgnoreCase(countyId, "CJ-NAP");
        verify(cityRepository, never()).save(any());

        verifyNoInteractions(countryRepository);
        verifyNoMoreInteractions(countyRepository, cityRepository);
    }

    @Test
    void createCityShouldSaveWhenCityCodeIsUniqueInCounty() {
        Long countyId = 10L;
        County county = mock(County.class);
        when(countyRepository.findById(countyId)).thenReturn(Optional.of(county));
        when(cityRepository.findByCounty_IdAndCodeIgnoreCase(countyId, "CJ-NAP")).thenReturn(Optional.empty());

        City saved = mock(City.class);
        when(cityRepository.save(any(City.class))).thenReturn(saved);

        City result = geographyService.createCity(countyId, "Cluj-Napoca", "CJ-NAP");

        assertSame(saved, result);

        verify(countyRepository).findById(countyId);
        verify(cityRepository).findByCounty_IdAndCodeIgnoreCase(countyId, "CJ-NAP");

        ArgumentCaptor<City> captor = ArgumentCaptor.forClass(City.class);
        verify(cityRepository).save(captor.capture());
        assertNotNull(captor.getValue(), "City passed to save() must not be null");

        verifyNoInteractions(countryRepository);
        verifyNoMoreInteractions(countyRepository, cityRepository);
    }
}
