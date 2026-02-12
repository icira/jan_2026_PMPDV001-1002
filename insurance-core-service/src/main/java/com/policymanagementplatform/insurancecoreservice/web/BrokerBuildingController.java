package com.policymanagementplatform.insurancecoreservice.web;

import com.policymanagementplatform.insurancecoreservice.domain.Building;
import com.policymanagementplatform.insurancecoreservice.service.BuildingService;
import com.policymanagementplatform.insurancecoreservice.web.dto.CreateBuildingRequest;
import com.policymanagementplatform.insurancecoreservice.web.dto.UpdateBuildingRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/*
LANES ADDED:
- Passes risk flags from DTO to BuildingService.
WHY:
- Part 2: risk indicators influence premium calculation via FeeConfiguration risk adjustments.
*/
@RestController
@RequestMapping("/api/brokers")
public class BrokerBuildingController {

    private final BuildingService buildingService;

    public BrokerBuildingController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    @GetMapping("/clients/{clientId}/buildings")
    public Page<Building> listByClient(
            @PathVariable Long clientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return buildingService.listByClient(clientId, pageable);
    }

    @PostMapping("/clients/{clientId}/buildings")
    public ResponseEntity<Building> create(
            @PathVariable Long clientId,
            @Valid @RequestBody CreateBuildingRequest req
    ) {
        Building created = buildingService.createForClient(
                clientId,
                req.cityId(),
                req.street(),
                req.number(),
                req.constructionYear(),
                req.type(),
                req.floors(),
                req.surfaceArea(),
                req.insuredValue(),
                req.earthquakeRiskZone(),
                req.floodRiskZone()
        );

        return ResponseEntity
                .created(URI.create("/api/brokers/buildings/" + created.getId()))
                .body(created);
    }

    @PutMapping("/buildings/{buildingId}")
    public Building update(
            @PathVariable Long buildingId,
            @Valid @RequestBody UpdateBuildingRequest req
    ) {
        return buildingService.update(
                buildingId,
                req.cityId(),
                req.street(),
                req.number(),
                req.constructionYear(),
                req.type(),
                req.floors(),
                req.surfaceArea(),
                req.insuredValue(),
                req.earthquakeRiskZone(),
                req.floodRiskZone()
        );
    }

    @GetMapping("/buildings/{buildingId}")
    public Building get(@PathVariable Long buildingId) {
        return buildingService.get(buildingId);
    }
}
