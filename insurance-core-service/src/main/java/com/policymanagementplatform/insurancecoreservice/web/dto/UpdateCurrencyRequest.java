package com.policymanagementplatform.insurancecoreservice.web.dto;

import jakarta.validation.constraints.*;

/*
LANES ADDED:
- UpdateCurrencyRequest DTO.
WHY:
- Part 2: admin updates currency and can activate/deactivate (with constraints).
*/
public record UpdateCurrencyRequest(
        @NotBlank @Size(max = 100) String name,
        @Positive double exchangeRateToBase,
        boolean active
) {}
