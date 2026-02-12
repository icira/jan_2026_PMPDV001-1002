package com.policymanagementplatform.insurancecoreservice.web.dto;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/*
LANES ADDED:
- CreatePolicyRequest DTO for broker draft policy creation.
WHY:
- Part 2: broker provides client/building/currency/basePremium/period and brokerId (for now input).
*/
public record CreatePolicyRequest(
        @NotNull Long clientId,
        @NotNull Long buildingId,
        @NotNull Long currencyId,
        @Positive double basePremium,
        @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        @NotNull Long brokerId
) {}
