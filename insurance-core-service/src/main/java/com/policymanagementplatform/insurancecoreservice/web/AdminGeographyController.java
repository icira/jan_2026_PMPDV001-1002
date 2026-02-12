package com.policymanagementplatform.insurancecoreservice.web;

import com.policymanagementplatform.insurancecoreservice.service.GeographyService;
import com.policymanagementplatform.insurancecoreservice.web.dto.GeographyDtos;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/admin/geography")
@Validated
public class AdminGeographyController {

    private final GeographyService geographyService;

    public AdminGeographyController(GeographyService geographyService) {
        this.geographyService = geographyService;
    }

    @PostMapping("/countries")
    public ResponseEntity<GeographyDtos.CountryResponse> createCountry(@RequestBody GeographyDtos.CreateCountryRequest req) {
        if (req == null || req.name() == null || req.name().isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        var saved = geographyService.createCountry(req.name().trim());
        return ResponseEntity.created(URI.create("/api/admin/geography/countries/" + saved.getId()))
                .body(new GeographyDtos.CountryResponse(saved.getId(), saved.getName()));
    }

    @PostMapping("/counties")
    public ResponseEntity<GeographyDtos.CountyResponse> createCounty(@RequestBody GeographyDtos.CreateCountyRequest req) {
        if (req == null || req.countryId() == null || req.name() == null || req.name().isBlank()) {
            throw new IllegalArgumentException("countryId and name are required");
        }
        var saved = geographyService.createCounty(req.countryId(), req.name().trim());
        return ResponseEntity.created(URI.create("/api/admin/geography/counties/" + saved.getId()))
                .body(new GeographyDtos.CountyResponse(saved.getId(), saved.getCountry().getId(), saved.getName()));
    }

    @PostMapping("/cities")
    public ResponseEntity<GeographyDtos.CityResponse> createCity(@RequestBody GeographyDtos.CreateCityRequest req) {
        if (req == null || req.countyId() == null || req.name() == null || req.name().isBlank()
                || req.code() == null || req.code().isBlank()) {
            throw new IllegalArgumentException("countyId, name and code are required");
        }
        var saved = geographyService.createCity(req.countyId(), req.name().trim(), req.code().trim());
        return ResponseEntity.created(URI.create("/api/admin/geography/cities/" + saved.getId()))
                .body(new GeographyDtos.CityResponse(saved.getId(), saved.getCounty().getId(), saved.getName(), saved.getCode()));
    }
}
