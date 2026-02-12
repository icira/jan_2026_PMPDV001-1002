package com.policymanagementplatform.insurancecoreservice.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "cities",
        uniqueConstraints = @UniqueConstraint(name = "uk_city_county_code", columnNames = {"county_id", "code"}))
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "county_id", nullable = false, foreignKey = @ForeignKey(name = "fk_city_county"))
    private County county;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 20)
    private String code;

    protected City() { }

    public City(County county, String name, String code) {
        this.county = county;
        this.name = name;
        this.code = code;
    }

    public Long getId() { return id; }
    public County getCounty() { return county; }
    public String getName() { return name; }
    public String getCode() { return code; }
}
