package com.policymanagementplatform.insurancecoreservice.domain;

import jakarta.persistence.*;

import java.time.LocalDate;

/*
LANES ADDED:
- FeeConfiguration entity with effective date range + active flag.
WHY:
- Part 2: final premium uses active fee configurations; out-of-date/inactive are ignored.
*/
@Entity
@Table(name = "fee_configurations")
public class FeeConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private FeeConfigurationType type;

    @Column(nullable = false)
    private double percentage;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Column(nullable = false)
    private boolean active;

    protected FeeConfiguration() { }

    public FeeConfiguration(String name, FeeConfigurationType type, double percentage, LocalDate effectiveFrom, LocalDate effectiveTo, boolean active) {
        this.name = name;
        this.type = type;
        this.percentage = percentage;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
        this.active = active;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public FeeConfigurationType getType() { return type; }
    public double getPercentage() { return percentage; }
    public LocalDate getEffectiveFrom() { return effectiveFrom; }
    public LocalDate getEffectiveTo() { return effectiveTo; }
    public boolean isActive() { return active; }

    public void update(double percentage, LocalDate effectiveFrom, LocalDate effectiveTo, boolean active) {
        this.percentage = percentage;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
        this.active = active;
    }

    public boolean isEffectiveOn(LocalDate date) {
        if (!active) return false;
        if (date.isBefore(effectiveFrom)) return false;
        return effectiveTo == null || !date.isAfter(effectiveTo);
    }
}
