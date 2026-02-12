-- LANES ADDED:
-- - Full schema: geography + clients + buildings + identifier change audit.

CREATE TABLE countries (
                           id BIGSERIAL PRIMARY KEY,
                           name VARCHAR(100) NOT NULL,
                           CONSTRAINT uk_country_name UNIQUE (name)
);

CREATE TABLE counties (
                          id BIGSERIAL PRIMARY KEY,
                          country_id BIGINT NOT NULL,
                          name VARCHAR(100) NOT NULL,
                          CONSTRAINT fk_county_country FOREIGN KEY (country_id) REFERENCES countries(id),
                          CONSTRAINT uk_county_country_name UNIQUE (country_id, name)
);

CREATE TABLE cities (
                        id BIGSERIAL PRIMARY KEY,
                        county_id BIGINT NOT NULL,
                        name VARCHAR(120) NOT NULL,
                        code VARCHAR(20) NOT NULL,
                        CONSTRAINT fk_city_county FOREIGN KEY (county_id) REFERENCES counties(id),
                        CONSTRAINT uk_city_county_code UNIQUE (county_id, code)
);

CREATE TABLE clients (
                         id BIGSERIAL PRIMARY KEY,
                         type VARCHAR(20) NOT NULL,
                         name VARCHAR(200) NOT NULL,
                         identification_number VARCHAR(32) NOT NULL,
                         email VARCHAR(200),
                         phone VARCHAR(50),
                         primary_address VARCHAR(300),
                         created_at TIMESTAMPTZ NOT NULL,
                         updated_at TIMESTAMPTZ NOT NULL,
                         CONSTRAINT uk_client_identifier UNIQUE (identification_number)
);

CREATE TABLE client_identifier_changes (
                                           id BIGSERIAL PRIMARY KEY,
                                           client_id BIGINT NOT NULL,
                                           old_value VARCHAR(32) NOT NULL,
                                           new_value VARCHAR(32) NOT NULL,
                                           changed_by VARCHAR(100) NOT NULL,
                                           changed_at TIMESTAMPTZ NOT NULL,
                                           CONSTRAINT fk_change_client FOREIGN KEY (client_id) REFERENCES clients(id)
);

CREATE TABLE buildings (
                           id BIGSERIAL PRIMARY KEY,
                           owner_client_id BIGINT NOT NULL,
                           city_id BIGINT NOT NULL,
                           street VARCHAR(200) NOT NULL,
                           number VARCHAR(50) NOT NULL,
                           construction_year INT NOT NULL,
                           type VARCHAR(30) NOT NULL,
                           floors INT NOT NULL,
                           surface_area DOUBLE PRECISION NOT NULL,
                           insured_value DOUBLE PRECISION NOT NULL,
                           CONSTRAINT fk_building_client FOREIGN KEY (owner_client_id) REFERENCES clients(id),
                           CONSTRAINT fk_building_city FOREIGN KEY (city_id) REFERENCES cities(id)
);

-- Helpful indexes for search/listing:
CREATE INDEX idx_counties_country ON counties(country_id);
CREATE INDEX idx_cities_county ON cities(county_id);
CREATE INDEX idx_buildings_owner ON buildings(owner_client_id);
CREATE INDEX idx_clients_name ON clients(name);
