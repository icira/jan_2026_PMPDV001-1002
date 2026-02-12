package com.policymanagementplatform.insurancecoreservice.domain;

import jakarta.persistence.*;

/*
LANES ADDED:
- Added earthquakeRiskZone and floodRiskZone fields + mapping.
- Extended constructor and updateDetails to include risk flags.
WHY:
- Part 2 Q&A: building risk indicators are related to FeeConfiguration risk adjustments.
*/
@Entity
@Table(name = "buildings")
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_client_id", nullable = false, foreignKey = @ForeignKey(name = "fk_building_client"))
    private Client owner;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false, foreignKey = @ForeignKey(name = "fk_building_city"))
    private City city;

    @Column(nullable = false, length = 200)
    private String street;

    @Column(nullable = false, length = 50)
    private String number;

    @Column(nullable = false)
    private int constructionYear;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BuildingType type;

    @Column(nullable = false)
    private int floors;

    @Column(nullable = false)
    private double surfaceArea;

    // implicit currency per Part 1 Q&A (stored as numeric only)
    @Column(nullable = false)
    private double insuredValue;

    // Part 2: risk indicators used for risk adjustment fees.
    @Column(nullable = false)
    private boolean earthquakeRiskZone;

    @Column(nullable = false)
    private boolean floodRiskZone;

    protected Building() { }

    public Building(Client owner,
                    City city,
                    String street,
                    String number,
                    int constructionYear,
                    BuildingType type,
                    int floors,
                    double surfaceArea,
                    double insuredValue,
                    boolean earthquakeRiskZone,
                    boolean floodRiskZone) {
        this.owner = owner;
        this.city = city;
        this.street = street;
        this.number = number;
        this.constructionYear = constructionYear;
        this.type = type;
        this.floors = floors;
        this.surfaceArea = surfaceArea;
        this.insuredValue = insuredValue;
        this.earthquakeRiskZone = earthquakeRiskZone;
        this.floodRiskZone = floodRiskZone;
    }

    public void updateDetails(City city,
                              String street,
                              String number,
                              int constructionYear,
                              BuildingType type,
                              int floors,
                              double surfaceArea,
                              double insuredValue,
                              boolean earthquakeRiskZone,
                              boolean floodRiskZone) {
        this.city = city;
        this.street = street;
        this.number = number;
        this.constructionYear = constructionYear;
        this.type = type;
        this.floors = floors;
        this.surfaceArea = surfaceArea;
        this.insuredValue = insuredValue;
        this.earthquakeRiskZone = earthquakeRiskZone;
        this.floodRiskZone = floodRiskZone;
    }

    public Long getId() { return id; }
    public Client getOwner() { return owner; }
    public City getCity() { return city; }
    public String getStreet() { return street; }
    public String getNumber() { return number; }
    public int getConstructionYear() { return constructionYear; }
    public BuildingType getType() { return type; }
    public int getFloors() { return floors; }
    public double getSurfaceArea() { return surfaceArea; }
    public double getInsuredValue() { return insuredValue; }
    public boolean isEarthquakeRiskZone() { return earthquakeRiskZone; }
    public boolean isFloodRiskZone() { return floodRiskZone; }
}
