package com.policymanagementplatform.insurancecoreservice.service;

import com.policymanagementplatform.insurancecoreservice.domain.*;
import com.policymanagementplatform.insurancecoreservice.repository.FeeConfigurationRepository;
import com.policymanagementplatform.insurancecoreservice.repository.RiskFactorConfigurationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PolicyCalculationServiceTest {

    /*
    LANES ADDED:
    - Tests for PolicyCalculationService (centralized premium calculation).
    WHY:
    - Part 2 requires a single calculation place and correct application of:
      broker commission + effective fees + risk factors + conditional risk flags.
    */

    private FeeConfigurationRepository feeRepository;
    private RiskFactorConfigurationRepository riskRepository;
    private PolicyCalculationService service;

    @BeforeEach
    void setUp() {
        feeRepository = mock(FeeConfigurationRepository.class);
        riskRepository = mock(RiskFactorConfigurationRepository.class);
        service = new PolicyCalculationService(feeRepository, riskRepository);
    }

    @Test
    void calculateFinalPremiumShouldThrowWhenBasePremiumNotPositive() {
        Broker broker = mock(Broker.class);
        Building building = mock(Building.class);

        assertThrows(IllegalArgumentException.class, () ->
                service.calculateFinalPremium(0.0, broker, building, LocalDate.of(2026, 2, 10))
        );
    }

    @Test
    void calculateFinalPremiumShouldReturnBaseWhenNoFeesNoRisksAndZeroCommission() {
        LocalDate date = LocalDate.of(2026, 2, 10);

        Broker broker = mock(Broker.class);
        when(broker.getCommissionPercentage()).thenReturn(0.0);

        Building building = mock(Building.class);
        City city = mock(City.class);
        County county = mock(County.class);
        Country country = mock(Country.class);

        when(building.getCity()).thenReturn(city);
        when(city.getCounty()).thenReturn(county);
        when(county.getCountry()).thenReturn(country);

        when(city.getId()).thenReturn(1L);
        when(county.getId()).thenReturn(2L);
        when(country.getId()).thenReturn(3L);

        when(building.getType()).thenReturn(BuildingType.RESIDENTIAL);
        when(building.isEarthquakeRiskZone()).thenReturn(false);
        when(building.isFloodRiskZone()).thenReturn(false);

        when(feeRepository.findEffectiveFees(date)).thenReturn(List.of());

        when(riskRepository.findByLevelAndReferenceIdAndActiveTrue(any(), anyLong())).thenReturn(List.of());

        double result = service.calculateFinalPremium(1000.0, broker, building, date);

        assertEquals(1000.0, result, 0.0001);
    }

    @Test
    void calculateFinalPremiumShouldApplyCommissionFeesAndRiskFactorsAndConditionalRiskFlags() {
        LocalDate date = LocalDate.of(2026, 2, 10);

        Broker broker = mock(Broker.class);
        when(broker.getCommissionPercentage()).thenReturn(5.0);

        Building building = mock(Building.class);

        City city = mock(City.class);
        County county = mock(County.class);
        Country country = mock(Country.class);

        when(building.getCity()).thenReturn(city);
        when(city.getCounty()).thenReturn(county);
        when(county.getCountry()).thenReturn(country);

        when(city.getId()).thenReturn(10L);
        when(county.getId()).thenReturn(20L);
        when(country.getId()).thenReturn(30L);

        when(building.getType()).thenReturn(BuildingType.INDUSTRIAL);

        when(building.isEarthquakeRiskZone()).thenReturn(true);
        when(building.isFloodRiskZone()).thenReturn(false);

        FeeConfiguration adminFee = new FeeConfiguration(
                "Admin fee",
                FeeConfigurationType.ADMIN_FEE,
                2.0,
                date.minusDays(1),
                null,
                true
        );

        FeeConfiguration earthquakeFee = new FeeConfiguration(
                "Earthquake",
                FeeConfigurationType.RISK_ADJUSTMENT_EARTHQUAKE,
                10.0,
                date.minusDays(1),
                null,
                true
        );

        FeeConfiguration floodFee = new FeeConfiguration(
                "Flood",
                FeeConfigurationType.RISK_ADJUSTMENT_FLOOD,
                7.0,
                date.minusDays(1),
                null,
                true
        );

        when(feeRepository.findEffectiveFees(date)).thenReturn(List.of(adminFee, earthquakeFee, floodFee));

        when(riskRepository.findByLevelAndReferenceIdAndActiveTrue(eq(RiskFactorLevel.CITY), eq(10L)))
                .thenReturn(List.of(new RiskFactorConfiguration(RiskFactorLevel.CITY, 10L, 3.0, true)));

        when(riskRepository.findByLevelAndReferenceIdAndActiveTrue(eq(RiskFactorLevel.COUNTY), eq(20L)))
                .thenReturn(List.of(new RiskFactorConfiguration(RiskFactorLevel.COUNTY, 20L, 1.0, true)));

        when(riskRepository.findByLevelAndReferenceIdAndActiveTrue(eq(RiskFactorLevel.COUNTRY), eq(30L)))
                .thenReturn(List.of(new RiskFactorConfiguration(RiskFactorLevel.COUNTRY, 30L, 0.5, true)));

        long buildingTypeId = BuildingType.INDUSTRIAL.ordinal();
        when(riskRepository.findByLevelAndReferenceIdAndActiveTrue(eq(RiskFactorLevel.BUILDING_TYPE), eq(buildingTypeId)))
                .thenReturn(List.of(new RiskFactorConfiguration(RiskFactorLevel.BUILDING_TYPE, buildingTypeId, 2.0, true)));

        double base = 1000.0;
        double result = service.calculateFinalPremium(base, broker, building, date);

        double expected = base * (1.0 + 23.5 / 100.0);

        assertEquals(expected, result, 0.0001);
    }
}
