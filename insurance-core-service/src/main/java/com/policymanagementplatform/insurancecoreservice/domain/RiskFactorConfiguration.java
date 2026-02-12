package com.policymanagementplatform.insurancecoreservice.domain;

import jakarta.persistence.*;

/*
LANES ADDED:
- RiskFactorConfiguration entity.
WHY:
- Part 2: active risk factor adjustments are added (summed) when calculating final premium.
- Q&A #2: interacts with Building via location (country/county/city) and BuildingType.
*/
@Entity
@Table(name = "risk_factor_configurations")
public class RiskFactorConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RiskFactorLevel level;

    @Column(name = "reference_id", nullable = false)
    private Long referenceId;

    @Column(name = "adjustment_percentage", nullable = false)
    private double adjustmentPercentage;

    @Column(nullable = false)
    private boolean active;

    protected RiskFactorConfiguration() { }

    public RiskFactorConfiguration(RiskFactorLevel level, Long referenceId, double adjustmentPercentage, boolean active) {
        this.level = level;
        this.referenceId = referenceId;
        this.adjustmentPercentage = adjustmentPercentage;
        this.active = active;
    }

    public Long getId() { return id; }
    public RiskFactorLevel getLevel() { return level; }
    public Long getReferenceId() { return referenceId; }
    public double getAdjustmentPercentage() { return adjustmentPercentage; }
    public boolean isActive() { return active; }

    public void update(double adjustmentPercentage, boolean active) {
        this.adjustmentPercentage = adjustmentPercentage;
        this.active = active;
    }
}
