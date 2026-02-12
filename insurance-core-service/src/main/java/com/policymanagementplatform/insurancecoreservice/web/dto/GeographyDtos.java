package com.policymanagementplatform.insurancecoreservice.web.dto;

public final class GeographyDtos {
    private GeographyDtos() {}

    public record CountryResponse(Long id, String name) {}
    public record CountyResponse(Long id, Long countryId, String name) {}
    public record CityResponse(Long id, Long countyId, String name, String code) {}

    public record CreateCountryRequest(String name) {}
    public record CreateCountyRequest(Long countryId, String name) {}
    public record CreateCityRequest(Long countyId, String name, String code) {}
}
