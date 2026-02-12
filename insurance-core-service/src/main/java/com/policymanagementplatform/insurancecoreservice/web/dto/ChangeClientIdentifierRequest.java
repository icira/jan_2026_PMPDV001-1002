package com.policymanagementplatform.insurancecoreservice.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeClientIdentifierRequest(
        @NotBlank @Size(max = 32) String newIdentificationNumber
) { }
