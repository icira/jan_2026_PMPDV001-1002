package com.policymanagementplatform.insurancecoreservice.domain;

import jakarta.persistence.*;

/*
LANES ADDED:
- Broker entity with brokerCode (unique), status, commissionPercentage.
WHY:
- Part 2: admin manages brokers and deactivated brokers cannot create new policies.
- Q&A #1: Broker Code is unique business code; DB id is internal unique identifier.
- Q&A #3: apply both broker commissionPercentage and FeeConfiguration percentages.
*/
@Entity
@Table(
        name = "brokers",
        uniqueConstraints = @UniqueConstraint(name = "uk_broker_code", columnNames = "broker_code")
)
public class Broker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "broker_code", nullable = false, length = 50)
    private String brokerCode;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 200)
    private String email;

    @Column(length = 50)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BrokerStatus status;

    @Column(nullable = false)
    private double commissionPercentage;

    protected Broker() { }

    public Broker(String brokerCode, String name, String email, String phone, double commissionPercentage, BrokerStatus status) {
        this.brokerCode = brokerCode;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.commissionPercentage = commissionPercentage;
        this.status = status;
    }

    public Long getId() { return id; }
    public String getBrokerCode() { return brokerCode; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public BrokerStatus getStatus() { return status; }
    public double getCommissionPercentage() { return commissionPercentage; }

    public void update(String name, String email, String phone, double commissionPercentage) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.commissionPercentage = commissionPercentage;
    }

    public void activate() { this.status = BrokerStatus.ACTIVE; }
    public void deactivate() { this.status = BrokerStatus.INACTIVE; }
}
