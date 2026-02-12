-- LANES ADDED:
-- - Seed minimal Romania geography sample.

INSERT INTO countries(name) VALUES ('Romania')
    ON CONFLICT (name) DO NOTHING;

-- Counties (sample)
WITH ro AS (SELECT id FROM countries WHERE name = 'Romania')
INSERT INTO counties(country_id, name)
SELECT ro.id, v.name
FROM ro
         JOIN (VALUES
                   ('Cluj'),
                   ('Bihor'),
                   ('Bucuresti')
) AS v(name) ON TRUE
    ON CONFLICT DO NOTHING;

-- Cities (sample; code = simple abbreviation)
WITH
    cluj_county AS (SELECT id FROM counties WHERE name = 'Cluj'),
    bihor_county AS (SELECT id FROM counties WHERE name = 'Bihor'),
    buc_county  AS (SELECT id FROM counties WHERE name = 'Bucuresti')
INSERT INTO cities(county_id, name, code)
SELECT c.id, v.name, v.code
FROM cluj_county c
         JOIN (VALUES
                   ('Cluj-Napoca', 'CJ-NAP'),
                   ('Turda', 'CJ-TUR')
) AS v(name, code) ON TRUE
    ON CONFLICT DO NOTHING;

WITH bih AS (SELECT id FROM counties WHERE name = 'Bihor')
INSERT INTO cities(county_id, name, code)
SELECT bih.id, v.name, v.code
FROM bih
         JOIN (VALUES
                   ('Oradea', 'BH-ORA')
) AS v(name, code) ON TRUE
    ON CONFLICT DO NOTHING;

WITH buc AS (SELECT id FROM counties WHERE name = 'Bucuresti')
INSERT INTO cities(county_id, name, code)
SELECT buc.id, v.name, v.code
FROM buc
         JOIN (VALUES
                   ('Bucuresti', 'B-001')
) AS v(name, code) ON TRUE
    ON CONFLICT DO NOTHING;
