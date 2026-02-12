package com.policymanagementplatform.insurancecoreservice.web;

import com.policymanagementplatform.insurancecoreservice.domain.FeeConfiguration;
import com.policymanagementplatform.insurancecoreservice.service.FeeConfigurationAdminService;
import com.policymanagementplatform.insurancecoreservice.web.dto.CreateFeeRequest;
import com.policymanagementplatform.insurancecoreservice.web.dto.UpdateFeeRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
LANES ADDED:
- AdminFeeConfigurationController endpoints.
WHY:
- Part 2: admins manage fee configurations.
*/
@RestController
@RequestMapping("/api/admin/fees")
public class AdminFeeConfigurationController {

    private final FeeConfigurationAdminService feeService;

    public AdminFeeConfigurationController(FeeConfigurationAdminService feeService) {
        this.feeService = feeService;
    }

    @GetMapping
    public List<FeeConfiguration> list() {
        return feeService.list();
    }

    @PostMapping
    public FeeConfiguration create(@Valid @RequestBody CreateFeeRequest req) {
        return feeService.create(req.name(), req.type(), req.percentage(), req.effectiveFrom(), req.effectiveTo(), req.active());
    }

    @PutMapping("/{id}")
    public FeeConfiguration update(@PathVariable Long id, @Valid @RequestBody UpdateFeeRequest req) {
        return feeService.update(id, req.percentage(), req.effectiveFrom(), req.effectiveTo(), req.active());
    }
}
