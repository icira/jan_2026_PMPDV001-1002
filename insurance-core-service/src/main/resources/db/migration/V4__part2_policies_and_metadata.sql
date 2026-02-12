-- LANES ADDED:
-- - Part 2 schema: brokers, currencies, fee_configurations, risk_factor_configurations, policies.
-- WHY:
-- - Part 2 introduces policy management + admin metadata.

CREATE TABLE IF NOT EXISTS brokers (
                         id BIGSERIAL PRIMARY KEY,
                         broker_code VARCHAR(50) NOT NULL,
                         name VARCHAR(200) NOT NULL,
                         email VARCHAR(200),
                         phone VARCHAR(50),
                         status VARCHAR(20) NOT NULL,
                         commission_percentage DOUBLE PRECISION NOT NULL DEFAULT 0,
                         CONSTRAINT uk_broker_code UNIQUE (broker_code)
);

CREATE TABLE IF NOT EXISTS currencies (
                            id BIGSERIAL PRIMARY KEY,
                            code VARCHAR(10) NOT NULL,
                            name VARCHAR(100) NOT NULL,
                            exchange_rate_to_base DOUBLE PRECISION NOT NULL,
                            active BOOLEAN NOT NULL DEFAULT TRUE,
                            CONSTRAINT uk_currency_code UNIQUE (code)
);

CREATE TABLE IF NOT EXISTS fee_configurations (
                                    id BIGSERIAL PRIMARY KEY,
                                    name VARCHAR(200) NOT NULL,
                                    type VARCHAR(40) NOT NULL,
                                    percentage DOUBLE PRECISION NOT NULL,
                                    effective_from DATE NOT NULL,
                                    effective_to DATE,
                                    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS risk_factor_configurations (
                                            id BIGSERIAL PRIMARY KEY,
                                            level VARCHAR(30) NOT NULL,
                                            reference_id BIGINT NOT NULL,
                                            adjustment_percentage DOUBLE PRECISION NOT NULL,
                                            active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS policies (
                          id BIGSERIAL PRIMARY KEY,
                          policy_number VARCHAR(50) NOT NULL,
                          client_id BIGINT NOT NULL,
                          building_id BIGINT NOT NULL,
                          broker_id BIGINT NOT NULL,
                          currency_id BIGINT NOT NULL,
                          status VARCHAR(20) NOT NULL,
                          start_date DATE NOT NULL,
                          end_date DATE NOT NULL,
                          base_premium DOUBLE PRECISION NOT NULL,
                          final_premium DOUBLE PRECISION NOT NULL,
                          created_at TIMESTAMPTZ NOT NULL,
                          updated_at TIMESTAMPTZ NOT NULL,
                          cancellation_reason VARCHAR(500),
                          cancellation_date DATE,
                          CONSTRAINT uk_policy_number UNIQUE (policy_number),
                          CONSTRAINT fk_policy_client FOREIGN KEY (client_id) REFERENCES clients(id),
                          CONSTRAINT fk_policy_building FOREIGN KEY (building_id) REFERENCES buildings(id),
                          CONSTRAINT fk_policy_broker FOREIGN KEY (broker_id) REFERENCES brokers(id),
                          CONSTRAINT fk_policy_currency FOREIGN KEY (currency_id) REFERENCES currencies(id)
);

CREATE INDEX idx_policy_client ON policies(client_id);
CREATE INDEX idx_policy_broker ON policies(broker_id);
CREATE INDEX idx_policy_status ON policies(status);
CREATE INDEX idx_policy_start_date ON policies(start_date);
