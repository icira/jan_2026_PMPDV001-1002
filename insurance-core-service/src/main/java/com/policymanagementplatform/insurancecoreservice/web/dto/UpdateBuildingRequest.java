package com.policymanagementplatform.insurancecoreservice.web.dto;

import com.policymanagementplatform.insurancecoreservice.domain.BuildingType;
import jakarta.validation.constraints.*;

/*
LANES ADDED:
- Added earthquakeRiskZone and floodRiskZone booleans.
WHY:
- Part 2: building risk indicators affect premium risk adjustment fees.
*/
public record UpdateBuildingRequest(
        @NotNull Long cityId,
        @NotBlank @Size(max = 200) String street,
        @NotBlank @Size(max = 50) String number,
        @Min(1800) @Max(2100) int constructionYear,
        @NotNull BuildingType type,
        @Min(1) @Max(500) int floors,
        @Positive double surfaceArea,
        @Positive double insuredValue,
        boolean earthquakeRiskZone,
        boolean floodRiskZone
) {}
