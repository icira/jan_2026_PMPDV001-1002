package com.policymanagementplatform.insurancecoreservice.web;

import com.policymanagementplatform.insurancecoreservice.domain.Policy;
import com.policymanagementplatform.insurancecoreservice.domain.PolicyStatus;
import com.policymanagementplatform.insurancecoreservice.service.PolicyService;
import com.policymanagementplatform.insurancecoreservice.web.dto.CancelPolicyRequest;
import com.policymanagementplatform.insurancecoreservice.web.dto.CreatePolicyRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;

/*
LANES ADDED:
- Renamed the "endDate" request param variable from startDateTo -> endDateTo.
WHY:
- Part 2 defines broker policy listing filters as startDate + endDate (period bounds).
- Using a correct variable name prevents subtle bugs and matches the API spec meaning. :contentReference[oaicite:1]{index=1}
*/
@RestController
@RequestMapping("/api/brokers/policies")
public class BrokerPolicyController {

    private final PolicyService policyService;

    public BrokerPolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @GetMapping
    public Page<Policy> list(
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) Long brokerId,
            @RequestParam(required = false) PolicyStatus status,
            @RequestParam(name = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDateFrom,

            // LANES ADDED: renamed variable to endDateTo
            // What: bind "endDate" into endDateTo
            // Why: it represents the upper bound of the period filter (policy end date).
            @RequestParam(name = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDateTo,

            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        // LANES ADDED: pass endDateTo into service instead of misleading name
        return policyService.search(clientId, brokerId, status, startDateFrom, endDateTo, pageable);
    }

    @GetMapping("/{policyId}")
    public Policy get(@PathVariable Long policyId) {
        return policyService.get(policyId);
    }

    @PostMapping
    public ResponseEntity<Policy> createDraft(@Valid @RequestBody CreatePolicyRequest req) {
        Policy created = policyService.createDraft(
                req.clientId(),
                req.buildingId(),
                req.currencyId(),
                req.basePremium(),
                req.startDate(),
                req.endDate(),
                req.brokerId()
        );
        return ResponseEntity.created(URI.create("/api/brokers/policies/" + created.getId())).body(created);
    }

    @PostMapping("/{policyId}/activate")
    public Policy activate(@PathVariable Long policyId) {
        return policyService.activate(policyId, LocalDate.now());
    }

    @PostMapping("/{policyId}/cancel")
    public Policy cancel(@PathVariable Long policyId, @Valid @RequestBody CancelPolicyRequest req) {
        // cancellationDate is server-side "today" as per your design comment in DTO.
        return policyService.cancel(policyId, req.reason(), LocalDate.now());
    }
}
