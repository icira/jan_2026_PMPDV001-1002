package com.policymanagementplatform.insurancecoreservice.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "client_identifier_changes")
public class ClientIdentifierChange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false, foreignKey = @ForeignKey(name = "fk_change_client"))
    private Client client;

    @Column(nullable = false, length = 32)
    private String oldValue;

    @Column(nullable = false, length = 32)
    private String newValue;

    @Column(nullable = false, length = 100)
    private String changedBy; // placeholder for future auth integration

    @Column(nullable = false)
    private Instant changedAt;

    protected ClientIdentifierChange() { }

    public ClientIdentifierChange(Client client, String oldValue, String newValue, String changedBy) {
        this.client = client;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.changedBy = changedBy;
        this.changedAt = Instant.now();
    }
}
