package com.policymanagementplatform.insurancecoreservice.web.dto;

import jakarta.validation.constraints.*;

/*
LANES ADDED:
- CreateCurrencyRequest DTO.
WHY:
- Part 2: admin manages currencies.
*/
public record CreateCurrencyRequest(
        @NotBlank @Size(max = 10) String code,
        @NotBlank @Size(max = 100) String name,
        @Positive double exchangeRateToBase,
        boolean active
) {}
