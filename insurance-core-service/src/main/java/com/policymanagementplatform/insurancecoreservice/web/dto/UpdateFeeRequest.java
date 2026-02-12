package com.policymanagementplatform.insurancecoreservice.web.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/*
LANES ADDED:
- UpdateFeeRequest DTO.
WHY:
- Part 2: update fee percentage/validity/active.
*/
public record UpdateFeeRequest(
        double percentage,
        @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate effectiveFrom,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate effectiveTo,
        boolean active
) {}
