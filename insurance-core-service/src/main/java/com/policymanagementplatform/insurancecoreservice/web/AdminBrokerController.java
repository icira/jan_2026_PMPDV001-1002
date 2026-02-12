package com.policymanagementplatform.insurancecoreservice.web;

import com.policymanagementplatform.insurancecoreservice.domain.Broker;
import com.policymanagementplatform.insurancecoreservice.domain.BrokerStatus;
import com.policymanagementplatform.insurancecoreservice.service.BrokerService;
import com.policymanagementplatform.insurancecoreservice.web.dto.CreateBrokerRequest;
import com.policymanagementplatform.insurancecoreservice.web.dto.UpdateBrokerRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

/*
LANES ADDED:
- AdminBrokerController implements admin broker endpoints.
WHY:
- Part 2: admins manage brokers and activate/deactivate them.
*/
@RestController
@RequestMapping("/api/admin/brokers")
public class AdminBrokerController {

    private final BrokerService brokerAdminService;

    public AdminBrokerController(BrokerService brokerAdminService) {
        this.brokerAdminService = brokerAdminService;
    }

    @GetMapping
    public Page<Broker> list(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return brokerAdminService.list(pageable);
    }

    @GetMapping("/{id}")
    public Broker get(@PathVariable Long id) {
        return brokerAdminService.get(id);
    }

    @PostMapping
    public Broker create(@Valid @RequestBody CreateBrokerRequest req) {
        return brokerAdminService.create(
                req.brokerCode(),
                req.name(),
                req.email(),
                req.phone(),
                req.commissionPercentage(),
                req.initialStatus()
        );
    }

    @PutMapping("/{id}")
    public Broker update(@PathVariable Long id, @Valid @RequestBody UpdateBrokerRequest req) {
        return brokerAdminService.update(id, req.name(), req.email(), req.phone(), req.commissionPercentage());
    }

    @PostMapping("/{id}/activate")
    public Broker activate(@PathVariable Long id) {
        return brokerAdminService.changeStatus(id, BrokerStatus.ACTIVE);
    }

    @PostMapping("/{id}/deactivate")
    public Broker deactivate(@PathVariable Long id) {
        return brokerAdminService.changeStatus(id, BrokerStatus.INACTIVE);
    }
}
