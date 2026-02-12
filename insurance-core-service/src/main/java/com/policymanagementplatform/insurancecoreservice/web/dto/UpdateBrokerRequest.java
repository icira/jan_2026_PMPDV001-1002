package com.policymanagementplatform.insurancecoreservice.web.dto;

import jakarta.validation.constraints.*;

/*
LANES ADDED:
- UpdateBrokerRequest DTO for admin broker update.
WHY:
- Part 2: admin updates broker details.
*/
public record UpdateBrokerRequest(
        @NotBlank @Size(max = 200) String name,
        @Email @Size(max = 200) String email,
        @Size(max = 50) String phone,
        @PositiveOrZero double commissionPercentage
) {}
