package com.policymanagementplatform.insurancecoreservice.web.dto;

import com.policymanagementplatform.insurancecoreservice.domain.ClientType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateClientRequest(
        @NotNull ClientType type,
        @NotBlank @Size(max = 200) String name,
        @NotBlank @Size(max = 32) String identificationNumber,
        @Email @Size(max = 200) String email,
        @Size(max = 50) String phone,
        @Size(max = 300) String primaryAddress
) {}
