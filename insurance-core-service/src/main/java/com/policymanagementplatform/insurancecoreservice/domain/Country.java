package com.policymanagementplatform.insurancecoreservice.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "countries", uniqueConstraints = @UniqueConstraint(name = "uk_country_name", columnNames = "name"))
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    protected Country() { }

    public Country(String name) {
        this.name = name;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
}
