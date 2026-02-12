package com.policymanagementplatform.insurancecoreservice.web.dto;

import jakarta.validation.constraints.NotNull;

/*
LANES ADDED:
- UpdateRiskFactorRequest DTO.
WHY:
- Part 2: admin updates risk factor adjustment.
*/
public record UpdateRiskFactorRequest(
        double adjustmentPercentage,
        boolean active
) {}
