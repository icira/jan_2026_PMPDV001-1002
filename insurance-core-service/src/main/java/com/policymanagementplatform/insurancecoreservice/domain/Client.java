package com.policymanagementplatform.insurancecoreservice.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "clients",
        uniqueConstraints = @UniqueConstraint(name = "uk_client_identifier", columnNames = "identification_number"))
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ClientType type;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "identification_number", nullable = false, length = 32)
    private String identificationNumber;

    @Column(length = 200)
    private String email;

    @Column(length = 50)
    private String phone;

    @Column(length = 300)
    private String primaryAddress;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected Client() { }

    public Client(ClientType type, String name, String identificationNumber, String email, String phone, String primaryAddress) {
        this.type = type;
        this.name = name;
        this.identificationNumber = identificationNumber;
        this.email = email;
        this.phone = phone;
        this.primaryAddress = primaryAddress;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void updateDetails(String name, String email, String phone, String primaryAddress) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.primaryAddress = primaryAddress;
        this.updatedAt = Instant.now();
    }

    public void changeIdentificationNumber(String newIdentifier) {
        this.identificationNumber = newIdentifier;
        this.updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public ClientType getType() { return type; }
    public String getName() { return name; }
    public String getIdentificationNumber() { return identificationNumber; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getPrimaryAddress() { return primaryAddress; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
