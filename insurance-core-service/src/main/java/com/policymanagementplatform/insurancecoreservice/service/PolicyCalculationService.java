package com.policymanagementplatform.insurancecoreservice.service;

import com.policymanagementplatform.insurancecoreservice.domain.*;
import com.policymanagementplatform.insurancecoreservice.repository.FeeConfigurationRepository;
import com.policymanagementplatform.insurancecoreservice.repository.RiskFactorConfigurationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/*
LANES ADDED:
- Central PolicyCalculationService.
WHY:
- Part 2 requires business logic out of controllers and a single coherent calculation service.
- Q&A #2/#3: sum broker commission + fee configs + risk factors; apply risk fees conditionally by building flags.
*/
@Service
public class PolicyCalculationService {

    private final FeeConfigurationRepository feeRepository;
    private final RiskFactorConfigurationRepository riskRepository;

    public PolicyCalculationService(FeeConfigurationRepository feeRepository,
                                    RiskFactorConfigurationRepository riskRepository) {
        this.feeRepository = feeRepository;
        this.riskRepository = riskRepository;
    }

    public double calculateFinalPremium(double basePremium, Broker broker, Building building, LocalDate startDate) {
        if (basePremium <= 0) {
            throw new IllegalArgumentException("Base premium must be positive");
        }

        double totalPercentage = 0.0;

        // (1) Broker-level commission (can be 0).
        totalPercentage += broker.getCommissionPercentage();

        // (2) Global fee configurations (only effective + active).
        List<FeeConfiguration> fees = feeRepository.findEffectiveFees(startDate);
        for (FeeConfiguration fee : fees) {
            totalPercentage += switch (fee.getType()) {
                case BROKER_COMMISSION, ADMIN_FEE, RISK_ADJUSTMENT_GENERIC -> fee.getPercentage();
                case RISK_ADJUSTMENT_EARTHQUAKE -> building.isEarthquakeRiskZone() ? fee.getPercentage() : 0.0;
                case RISK_ADJUSTMENT_FLOOD -> building.isFloodRiskZone() ? fee.getPercentage() : 0.0;
            };
        }

        // (3) Risk factor adjustments (location + building type).
        totalPercentage += sumRiskPercentages(building);

        // Final premium = basePremium * (1 + sumPercentages/100).
        return basePremium * (1.0 + totalPercentage / 100.0);
    }

    private double sumRiskPercentages(Building building) {
        double sum = 0.0;

        City city = building.getCity();
        County county = city.getCounty();
        Country country = county.getCountry();

        sum += riskRepository.findByLevelAndReferenceIdAndActiveTrue(RiskFactorLevel.CITY, city.getId())
                .stream().mapToDouble(RiskFactorConfiguration::getAdjustmentPercentage).sum();

        sum += riskRepository.findByLevelAndReferenceIdAndActiveTrue(RiskFactorLevel.COUNTY, county.getId())
                .stream().mapToDouble(RiskFactorConfiguration::getAdjustmentPercentage).sum();

        sum += riskRepository.findByLevelAndReferenceIdAndActiveTrue(RiskFactorLevel.COUNTRY, country.getId())
                .stream().mapToDouble(RiskFactorConfiguration::getAdjustmentPercentage).sum();

        long buildingTypeId = building.getType().ordinal();
        sum += riskRepository.findByLevelAndReferenceIdAndActiveTrue(RiskFactorLevel.BUILDING_TYPE, buildingTypeId)
                .stream().mapToDouble(RiskFactorConfiguration::getAdjustmentPercentage).sum();

        return sum;
    }
}
