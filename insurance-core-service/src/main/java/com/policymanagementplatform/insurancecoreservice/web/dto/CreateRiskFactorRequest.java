package com.policymanagementplatform.insurancecoreservice.web.dto;

import com.policymanagementplatform.insurancecoreservice.domain.RiskFactorLevel;
import jakarta.validation.constraints.*;

/*
LANES ADDED:
- CreateRiskFactorRequest DTO.
WHY:
- Part 2: admin defines risk adjustments by county/city/building type.
*/
public record CreateRiskFactorRequest(
        @NotNull RiskFactorLevel level,
        @NotNull Long referenceId,
        double adjustmentPercentage,
        boolean active
) {}
