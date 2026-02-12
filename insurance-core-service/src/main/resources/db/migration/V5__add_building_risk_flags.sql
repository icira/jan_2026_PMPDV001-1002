-- LANES ADDED:
-- - Adds Part 2 building risk indicators used in premium calculation.
-- WHY:
-- - Q&A: risk indicator fields from Building relate to FeeConfiguration risk adjustments.

ALTER TABLE buildings
    ADD COLUMN earthquake_risk_zone BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN flood_risk_zone BOOLEAN NOT NULL DEFAULT FALSE;
