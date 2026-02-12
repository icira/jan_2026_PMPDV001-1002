package com.policymanagementplatform.insurancecoreservice.domain;

import jakarta.persistence.*;

/*
LANES ADDED:
- Currency entity.
WHY:
- Part 2: admins manage currencies; policies select a currency.
*/
@Entity
@Table(
        name = "currencies",
        uniqueConstraints = @UniqueConstraint(name = "uk_currency_code", columnNames = "code")
)
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "exchange_rate_to_base", nullable = false)
    private double exchangeRateToBase;

    @Column(nullable = false)
    private boolean active;

    protected Currency() { }

    public Currency(String code, String name, double exchangeRateToBase, boolean active) {
        this.code = code;
        this.name = name;
        this.exchangeRateToBase = exchangeRateToBase;
        this.active = active;
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public double getExchangeRateToBase() { return exchangeRateToBase; }
    public boolean isActive() { return active; }

    public void update(String name, double exchangeRateToBase, boolean active) {
        this.name = name;
        this.exchangeRateToBase = exchangeRateToBase;
        this.active = active;
    }
}
