package com.policymanagementplatform.insurancecoreservice.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

/*
LANES ADDED (Policy.cancel validation section):
- Defensive checks for cancellation inputs:
  * reason must not be blank
  * cancellationDate must not be null
WHY:
- Domain invariants should be enforced in the domain model, not only in services.
- Prevents invalid persistent state if entity is used elsewhere later.
*/
@Entity
@Table(
        name = "policies",
        uniqueConstraints = @UniqueConstraint(name = "uk_policy_number", columnNames = "policy_number")
)
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "policy_number", nullable = false, length = 50)
    private String policyNumber;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false, foreignKey = @ForeignKey(name = "fk_policy_client"))
    private Client client;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false, foreignKey = @ForeignKey(name = "fk_policy_building"))
    private Building building;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "broker_id", nullable = false, foreignKey = @ForeignKey(name = "fk_policy_broker"))
    private Broker broker;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_id", nullable = false, foreignKey = @ForeignKey(name = "fk_policy_currency"))
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PolicyStatus status;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "base_premium", nullable = false)
    private double basePremium;

    @Column(name = "final_premium", nullable = false)
    private double finalPremium;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    @Column(name = "cancellation_date")
    private LocalDate cancellationDate;

    protected Policy() { }

    public Policy(String policyNumber, Client client, Building building, Broker broker, Currency currency,
                  LocalDate startDate, LocalDate endDate, double basePremium, double finalPremium) {
        this.policyNumber = policyNumber;
        this.client = client;
        this.building = building;
        this.broker = broker;
        this.currency = currency;
        this.startDate = startDate;
        this.endDate = endDate;
        this.basePremium = basePremium;
        this.finalPremium = finalPremium;
        this.status = PolicyStatus.DRAFT;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void activate() {
        if (status != PolicyStatus.DRAFT) {
            throw new IllegalStateException("Only draft policies can be activated");
        }
        this.status = PolicyStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void cancel(String reason, LocalDate cancellationDate) {
        if (status != PolicyStatus.ACTIVE) {
            throw new IllegalStateException("Only active policies can be cancelled");
        }

        // LANES ADDED: defensive validation
        // What: ensure reason is present
        // Why: cancellation must have a reason (Part 2 requirement), and domain should enforce it.
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Cancellation reason is required");
        }

        // LANES ADDED: defensive validation
        // What: ensure cancellation date is present
        // Why: Part 2 requires effective cancellation date should be recorded.
        if (cancellationDate == null) {
            throw new IllegalArgumentException("Cancellation date is required");
        }

        this.status = PolicyStatus.CANCELLED;
        this.cancellationReason = reason;
        this.cancellationDate = cancellationDate;
        this.updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getPolicyNumber() { return policyNumber; }
    public Client getClient() { return client; }
    public Building getBuilding() { return building; }
    public Broker getBroker() { return broker; }
    public Currency getCurrency() { return currency; }
    public PolicyStatus getStatus() { return status; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public double getBasePremium() { return basePremium; }
    public double getFinalPremium() { return finalPremium; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public String getCancellationReason() { return cancellationReason; }
    public LocalDate getCancellationDate() { return cancellationDate; }

    public void refreshPremium(double basePremium, double finalPremium) {
        this.basePremium = basePremium;
        this.finalPremium = finalPremium;
        this.updatedAt = Instant.now();
    }
}
