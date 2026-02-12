package com.policymanagementplatform.insurancecoreservice.web;

import com.policymanagementplatform.insurancecoreservice.service.GeographyService;
import com.policymanagementplatform.insurancecoreservice.web.dto.GeographyDtos;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/brokers/geography")
public class BrokerGeographyController {

    private final GeographyService geographyService;

    public BrokerGeographyController(GeographyService geographyService) {
        this.geographyService = geographyService;
    }

    @GetMapping("/countries")
    public Page<GeographyDtos.CountryResponse> countries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return geographyService.listCountries(pageable)
                .map(c -> new GeographyDtos.CountryResponse(c.getId(), c.getName()));
    }

    @GetMapping("/countries/{countryId}/counties")
    public Page<GeographyDtos.CountyResponse> counties(
            @PathVariable Long countryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return geographyService.listCounties(countryId, pageable)
                .map(ct -> new GeographyDtos.CountyResponse(ct.getId(), ct.getCountry().getId(), ct.getName()));
    }

    @GetMapping("/counties/{countyId}/cities")
    public Page<GeographyDtos.CityResponse> cities(
            @PathVariable Long countyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "200") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return geographyService.listCities(countyId, pageable)
                .map(ci -> new GeographyDtos.CityResponse(ci.getId(), ci.getCounty().getId(), ci.getName(), ci.getCode()));
    }
}
