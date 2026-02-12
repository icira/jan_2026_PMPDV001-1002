package com.policymanagementplatform.insurancecoreservice.web.dto;

/*
LANES ADDED:
- DTO used by admin policy reports endpoint.
WHY:
- Part 2 requires reports by geography, broker and period with policy count and total value.
*/
public record PolicyReportRow(
        Long countryId,
        String countryName,
        Long countyId,
        String countyName,
        Long cityId,
        String cityName,
        Long brokerId,
        String brokerCode,
        String brokerName,
        long policyCount,
        double totalFinalPremium
) {
}