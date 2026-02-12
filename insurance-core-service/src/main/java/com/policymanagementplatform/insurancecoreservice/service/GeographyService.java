package com.policymanagementplatform.insurancecoreservice.service;

import com.policymanagementplatform.insurancecoreservice.domain.City;
import com.policymanagementplatform.insurancecoreservice.domain.Country;
import com.policymanagementplatform.insurancecoreservice.domain.County;
import com.policymanagementplatform.insurancecoreservice.exceptions.ConflictException;
import com.policymanagementplatform.insurancecoreservice.exceptions.NotFoundException;
import com.policymanagementplatform.insurancecoreservice.repository.CityRepository;
import com.policymanagementplatform.insurancecoreservice.repository.CountryRepository;
import com.policymanagementplatform.insurancecoreservice.repository.CountyRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GeographyService {

    private final CountryRepository countryRepository;
    private final CountyRepository countyRepository;
    private final CityRepository cityRepository;

    public GeographyService(CountryRepository countryRepository,
                            CountyRepository countyRepository,
                            CityRepository cityRepository) {
        this.countryRepository = countryRepository;
        this.countyRepository = countyRepository;
        this.cityRepository = cityRepository;
    }

    // ----------------------
    // Broker READ endpoints
    // ----------------------

    @Transactional(readOnly = true)
    public Page<Country> listCountries(Pageable pageable) {
        return countryRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<County> listCounties(Long countryId, Pageable pageable) {
        // If country doesn't exist, give a clean 404 instead of empty results.
        countryRepository.findById(countryId).orElseThrow(() -> new NotFoundException("Country not found"));
        return countyRepository.findByCountry_Id(countryId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<City> listCities(Long countyId, Pageable pageable) {
        countyRepository.findById(countyId).orElseThrow(() -> new NotFoundException("County not found"));
        return cityRepository.findByCounty_Id(countyId, pageable);
    }

    @Transactional(readOnly = true)
    public City getCity(Long cityId) {
        return cityRepository.findById(cityId).orElseThrow(() -> new NotFoundException("City not found"));
    }

    // ----------------------
    // Admin CREATE endpoints
    // ----------------------

    @Transactional
    public Country createCountry(String name) {
        countryRepository.findByNameIgnoreCase(name)
                .ifPresent(x -> { throw new ConflictException("Country name already exists"); });

        return countryRepository.save(new Country(name));
    }

    @Transactional
    public County createCounty(Long countryId, String name) {
        Country country = countryRepository.findById(countryId)
                .orElseThrow(() -> new NotFoundException("Country not found"));

        countyRepository.findByCountry_IdAndNameIgnoreCase(countryId, name)
                .ifPresent(x -> { throw new ConflictException("County name already exists for this country"); });

        return countyRepository.save(new County(country, name));
    }

    @Transactional
    public City createCity(Long countyId, String name, String code) {
        County county = countyRepository.findById(countyId)
                .orElseThrow(() -> new NotFoundException("County not found"));

        cityRepository.findByCounty_IdAndCodeIgnoreCase(countyId, code)
                .ifPresent(x -> { throw new ConflictException("City code already exists for this county"); });

        return cityRepository.save(new City(county, name, code));
    }
}