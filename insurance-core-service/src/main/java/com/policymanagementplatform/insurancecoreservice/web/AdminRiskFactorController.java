package com.policymanagementplatform.insurancecoreservice.web;

import com.policymanagementplatform.insurancecoreservice.domain.RiskFactorConfiguration;
import com.policymanagementplatform.insurancecoreservice.service.RiskFactorAdminService;
import com.policymanagementplatform.insurancecoreservice.web.dto.CreateRiskFactorRequest;
import com.policymanagementplatform.insurancecoreservice.web.dto.UpdateRiskFactorRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
LANES ADDED:
- AdminRiskFactorController endpoints.
WHY:
- Part 2: admins manage risk factor configurations.
*/
@RestController
@RequestMapping("/api/admin/risk-factors")
public class AdminRiskFactorController {

    private final RiskFactorAdminService riskService;

    public AdminRiskFactorController(RiskFactorAdminService riskService) {
        this.riskService = riskService;
    }

    @GetMapping
    public List<RiskFactorConfiguration> list() {
        return riskService.list();
    }

    @PostMapping
    public RiskFactorConfiguration create(@Valid @RequestBody CreateRiskFactorRequest req) {
        return riskService.create(req.level(), req.referenceId(), req.adjustmentPercentage(), req.active());
    }

    @PutMapping("/{id}")
    public RiskFactorConfiguration update(@PathVariable Long id, @Valid @RequestBody UpdateRiskFactorRequest req) {
        return riskService.update(id, req.adjustmentPercentage(), req.active());
    }
}
