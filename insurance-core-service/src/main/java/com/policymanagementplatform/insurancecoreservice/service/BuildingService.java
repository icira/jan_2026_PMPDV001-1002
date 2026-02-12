package com.policymanagementplatform.insurancecoreservice.service;

import com.policymanagementplatform.insurancecoreservice.domain.Building;
import com.policymanagementplatform.insurancecoreservice.domain.BuildingType;
import com.policymanagementplatform.insurancecoreservice.domain.City;
import com.policymanagementplatform.insurancecoreservice.domain.Client;
import com.policymanagementplatform.insurancecoreservice.exceptions.NotFoundException;
import com.policymanagementplatform.insurancecoreservice.repository.BuildingRepository;
import com.policymanagementplatform.insurancecoreservice.repository.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
LANES ADDED:
- createForClient/update now accept risk flags and pass them to Building.
WHY:
- Part 2: risk indicators are used in premium calculation via FeeConfiguration risk adjustment types.
*/
@Service
public class BuildingService {

    private static final Logger log = LoggerFactory.getLogger(BuildingService.class);

    private final BuildingRepository buildingRepository;
    private final ClientRepository clientRepository;
    private final GeographyService geographyService;

    public BuildingService(BuildingRepository buildingRepository,
                           ClientRepository clientRepository,
                           GeographyService geographyService) {
        this.buildingRepository = buildingRepository;
        this.clientRepository = clientRepository;
        this.geographyService = geographyService;
    }

    @Transactional
    public Building createForClient(Long clientId,
                                    Long cityId,
                                    String street,
                                    String number,
                                    int constructionYear,
                                    BuildingType type,
                                    int floors,
                                    double surfaceArea,
                                    double insuredValue,
                                    boolean earthquakeRiskZone,
                                    boolean floodRiskZone) {

        Client owner = clientRepository.findById(clientId)
                .orElseThrow(() -> new NotFoundException("Client not found"));

        City city = geographyService.getCity(cityId);

        Building building = new Building(
                owner, city, street, number, constructionYear, type, floors, surfaceArea, insuredValue,
                earthquakeRiskZone, floodRiskZone
        );
        Building saved = buildingRepository.save(building);

        log.info("Building created id={} ownerClientId={}", saved.getId(), owner.getId());
        return saved;
    }

    @Transactional
    public Building update(Long buildingId,
                           Long cityId,
                           String street,
                           String number,
                           int constructionYear,
                           BuildingType type,
                           int floors,
                           double surfaceArea,
                           double insuredValue,
                           boolean earthquakeRiskZone,
                           boolean floodRiskZone) {

        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new NotFoundException("Building not found"));

        // IMPORTANT RULE (Part 1): owner is not changeable via update.
        City city = geographyService.getCity(cityId);

        building.updateDetails(
                city, street, number, constructionYear, type, floors, surfaceArea, insuredValue,
                earthquakeRiskZone, floodRiskZone
        );
        return building;
    }

    @Transactional(readOnly = true)
    public Page<Building> listByClient(Long clientId, Pageable pageable) {
        clientRepository.findById(clientId).orElseThrow(() -> new NotFoundException("Client not found"));
        return buildingRepository.findByOwner_Id(clientId, pageable);
    }

    @Transactional(readOnly = true)
    public Building get(Long buildingId) {
        return buildingRepository.findById(buildingId)
                .orElseThrow(() -> new NotFoundException("Building not found"));
    }
}
