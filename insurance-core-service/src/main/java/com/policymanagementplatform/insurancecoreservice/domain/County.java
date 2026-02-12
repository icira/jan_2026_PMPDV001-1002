package com.policymanagementplatform.insurancecoreservice.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "counties",
        uniqueConstraints = @UniqueConstraint(name = "uk_county_country_name", columnNames = {"country_id", "name"}))
public class County {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false, foreignKey = @ForeignKey(name = "fk_county_country"))
    private Country country;

    @Column(nullable = false, length = 100)
    private String name;

    protected County() { }

    public County(Country country, String name) {
        this.country = country;
        this.name = name;
    }

    public Long getId() { return id; }
    public Country getCountry() { return country; }
    public String getName() { return name; }
}
