-- LANES ADDED:
-- - V3 demo seed: 1 country (Romania) + 24 counties + 24 cities + 24 clients + 24 buildings + 24 identifier-change audit rows.

-- ============================================================================
-- 1) COUNTRY (keep exactly 1 logical entry: Romania)
-- ============================================================================

INSERT INTO countries(name)
VALUES ('Romania')
    ON CONFLICT (name) DO NOTHING;

-- ============================================================================
-- 2) COUNTIES (24 rows) - must be unique per (country_id, name)
-- ============================================================================

WITH ro AS (SELECT id FROM countries WHERE name = 'Romania')
INSERT INTO counties(country_id, name)
SELECT ro.id, v.name
FROM ro
         JOIN (VALUES
                   ('Alba'),
                   ('Arad'),
                   ('Arges'),
                   ('Bacau'),
                   ('Bihor'),
                   ('Bistrita-Nasaud'),
                   ('Botosani'),
                   ('Braila'),
                   ('Brasov'),
                   ('Bucuresti'),
                   ('Buzau'),
                   ('Calarasi'),
                   ('Caras-Severin'),
                   ('Cluj'),
                   ('Constanta'),
                   ('Covasna'),
                   ('Dambovita'),
                   ('Dolj'),
                   ('Galati'),
                   ('Giurgiu'),
                   ('Gorj'),
                   ('Harghita'),
                   ('Hunedoara'),
                   ('Iasi')
) AS v(name) ON TRUE
    ON CONFLICT DO NOTHING;

-- ============================================================================
-- 3) CITIES (24 rows) - must be unique per (county_id, code)
-- ============================================================================

-- We insert 1 city per county to keep the cities table in the 20–30 range.
-- We align codes for the counties already used in V2 where applicable (CJ-NAP, BH-ORA, B-001).

WITH
    alba AS (SELECT id FROM counties WHERE name = 'Alba'),
    arad AS (SELECT id FROM counties WHERE name = 'Arad'),
    arges AS (SELECT id FROM counties WHERE name = 'Arges'),
    bacau AS (SELECT id FROM counties WHERE name = 'Bacau'),
    bihor AS (SELECT id FROM counties WHERE name = 'Bihor'),
    bn AS (SELECT id FROM counties WHERE name = 'Bistrita-Nasaud'),
    botosani AS (SELECT id FROM counties WHERE name = 'Botosani'),
    braila AS (SELECT id FROM counties WHERE name = 'Braila'),
    brasov AS (SELECT id FROM counties WHERE name = 'Brasov'),
    buc AS (SELECT id FROM counties WHERE name = 'Bucuresti'),
    buzau AS (SELECT id FROM counties WHERE name = 'Buzau'),
    calarasi AS (SELECT id FROM counties WHERE name = 'Calarasi'),
    caras AS (SELECT id FROM counties WHERE name = 'Caras-Severin'),
    cluj AS (SELECT id FROM counties WHERE name = 'Cluj'),
    constanta AS (SELECT id FROM counties WHERE name = 'Constanta'),
    covasna AS (SELECT id FROM counties WHERE name = 'Covasna'),
    dambovita AS (SELECT id FROM counties WHERE name = 'Dambovita'),
    dolj AS (SELECT id FROM counties WHERE name = 'Dolj'),
    galati AS (SELECT id FROM counties WHERE name = 'Galati'),
    giurgiu AS (SELECT id FROM counties WHERE name = 'Giurgiu'),
    gorj AS (SELECT id FROM counties WHERE name = 'Gorj'),
    harghita AS (SELECT id FROM counties WHERE name = 'Harghita'),
    hunedoara AS (SELECT id FROM counties WHERE name = 'Hunedoara'),
    iasi AS (SELECT id FROM counties WHERE name = 'Iasi')
INSERT INTO cities(county_id, name, code)
SELECT alba.id, 'Alba Iulia', 'AB-AI' FROM alba
UNION ALL SELECT arad.id, 'Arad', 'AR-ARAD' FROM arad
UNION ALL SELECT arges.id, 'Pitesti', 'AG-PIT' FROM arges
UNION ALL SELECT bacau.id, 'Bacau', 'BC-BC' FROM bacau
UNION ALL SELECT bihor.id, 'Oradea', 'BH-ORA' FROM bihor
UNION ALL SELECT bn.id, 'Bistrita', 'BN-BIS' FROM bn
UNION ALL SELECT botosani.id, 'Botosani', 'BT-BOT' FROM botosani
UNION ALL SELECT braila.id, 'Braila', 'BR-BRA' FROM braila
UNION ALL SELECT brasov.id, 'Brasov', 'BV-BV' FROM brasov
UNION ALL SELECT buc.id, 'Bucuresti', 'B-001' FROM buc
UNION ALL SELECT buzau.id, 'Buzau', 'BZ-BZ' FROM buzau
UNION ALL SELECT calarasi.id, 'Calarasi', 'CL-CL' FROM calarasi
UNION ALL SELECT caras.id, 'Resita', 'CS-RES' FROM caras
UNION ALL SELECT cluj.id, 'Cluj-Napoca', 'CJ-NAP' FROM cluj
UNION ALL SELECT constanta.id, 'Constanta', 'CT-CT' FROM constanta
UNION ALL SELECT covasna.id, 'Sfantu Gheorghe', 'CV-SG' FROM covasna
UNION ALL SELECT dambovita.id, 'Targoviste', 'DB-TGV' FROM dambovita
UNION ALL SELECT dolj.id, 'Craiova', 'DJ-CRV' FROM dolj
UNION ALL SELECT galati.id, 'Galati', 'GL-GL' FROM galati
UNION ALL SELECT giurgiu.id, 'Giurgiu', 'GR-GR' FROM giurgiu
UNION ALL SELECT gorj.id, 'Targu Jiu', 'GJ-TJ' FROM gorj
UNION ALL SELECT harghita.id, 'Miercurea Ciuc', 'HR-MC' FROM harghita
UNION ALL SELECT hunedoara.id, 'Deva', 'HD-DV' FROM hunedoara
UNION ALL SELECT iasi.id, 'Iasi', 'IS-IS' FROM iasi
    ON CONFLICT DO NOTHING;

-- ============================================================================
-- 4) CLIENTS (24 rows) - identification_number must be unique
-- ============================================================================

-- Mix of INDIVIDUAL and COMPANY.
-- identification_number: keep <= 32 chars, unique, and "current" value.
INSERT INTO clients(type, name, identification_number, email, phone, primary_address, created_at, updated_at)
VALUES
    ('INDIVIDUAL', 'Popescu Andrei',      'CNP-100000000001', 'andrei.popescu@example.com', '0700000001', 'Cluj-Napoca', now(), now()),
    ('INDIVIDUAL', 'Ionescu Maria',       'CNP-100000000002', 'maria.ionescu@example.com',  '0700000002', 'Bucuresti',  now(), now()),
    ('INDIVIDUAL', 'Georgescu Vlad',      'CNP-100000000003', 'vlad.georgescu@example.com', '0700000003', 'Brasov',     now(), now()),
    ('INDIVIDUAL', 'Stan Elena',          'CNP-100000000004', 'elena.stan@example.com',     '0700000004', 'Iasi',       now(), now()),
    ('INDIVIDUAL', 'Dumitru Mihai',       'CNP-100000000005', 'mihai.dumitru@example.com',  '0700000005', 'Constanta',  now(), now()),
    ('INDIVIDUAL', 'Radu Ioana',          'CNP-100000000006', 'ioana.radu@example.com',     '0700000006', 'Oradea',     now(), now()),
    ('INDIVIDUAL', 'Ilie Cristian',       'CNP-100000000007', 'cristian.ilie@example.com',  '0700000007', 'Craiova',    now(), now()),
    ('INDIVIDUAL', 'Marin Ana',           'CNP-100000000008', 'ana.marin@example.com',      '0700000008', 'Galati',     now(), now()),
    ('INDIVIDUAL', 'Petrescu Paul',       'CNP-100000000009', 'paul.petrescu@example.com',  '0700000009', 'Arad',       now(), now()),
    ('INDIVIDUAL', 'Enache Bianca',       'CNP-100000000010', 'bianca.enache@example.com',  '0700000010', 'Deva',       now(), now()),
    ('INDIVIDUAL', 'Tudor Sorin',         'CNP-100000000011', 'sorin.tudor@example.com',    '0700000011', 'Bacau',      now(), now()),
    ('INDIVIDUAL', 'Moldovan Oana',       'CNP-100000000012', 'oana.moldovan@example.com',  '0700000012', 'Alba Iulia', now(), now()),

    ('COMPANY', 'SC Carpathia Construct SRL', 'CUI-RO-20000001', 'office@carpathia.example.com', '0210000001', 'Bucuresti', now(), now()),
    ('COMPANY', 'SC Transilvania Tech SRL',   'CUI-RO-20000002', 'contact@trans-tech.example.com','0264000002', 'Cluj-Napoca', now(), now()),
    ('COMPANY', 'SC Dobrogea Logistics SA',   'CUI-RO-20000003', 'hello@dobrogea.example.com',    '0241000003', 'Constanta', now(), now()),
    ('COMPANY', 'SC Moldova Retail SRL',      'CUI-RO-20000004', 'sales@moldova-retail.example.com','0232000004', 'Iasi', now(), now()),
    ('COMPANY', 'SC Oltenia Industry SRL',    'CUI-RO-20000005', 'office@oltenia-ind.example.com', '0251000005', 'Craiova', now(), now()),
    ('COMPANY', 'SC Banat Services SRL',      'CUI-RO-20000006', 'support@banat.example.com',      '0256000006', 'Resita', now(), now()),
    ('COMPANY', 'SC Muntenia RealEstate SRL', 'CUI-RO-20000007', 'info@muntenia-re.example.com',   '0245000007', 'Targoviste', now(), now()),
    ('COMPANY', 'SC Delta Agro SRL',          'CUI-RO-20000008', 'contact@delta-agro.example.com', '0240000008', 'Braila', now(), now()),
    ('COMPANY', 'SC Harmonia Consulting SRL', 'CUI-RO-20000009', 'office@harmonia.example.com',    '0267000009', 'Miercurea Ciuc', now(), now()),
    ('COMPANY', 'SC Covasna Foods SRL',       'CUI-RO-20000010', 'hello@covasna-foods.example.com','0267000010', 'Sfantu Gheorghe', now(), now()),
    ('COMPANY', 'SC Giurgiu Trade SRL',       'CUI-RO-20000011', 'sales@giurgiu-trade.example.com','0246000011', 'Giurgiu', now(), now()),
    ('COMPANY', 'SC Bistrita Manufacturing SA','CUI-RO-20000012','office@bistrita-mfg.example.com','0263000012', 'Bistrita', now(), now())
    ON CONFLICT (identification_number) DO NOTHING;

-- ============================================================================
-- 5) BUILDINGS (24 rows) - must reference existing client and existing city
-- ============================================================================

-- We link 1 building per client to keep buildings in the 20–30 range.
-- Insured value is a positive amount (implicit currency), surface area positive.
WITH
    c AS (
        SELECT id, identification_number
        FROM clients
        WHERE identification_number IN (
                                        'CNP-100000000001','CNP-100000000002','CNP-100000000003','CNP-100000000004','CNP-100000000005','CNP-100000000006',
                                        'CNP-100000000007','CNP-100000000008','CNP-100000000009','CNP-100000000010','CNP-100000000011','CNP-100000000012',
                                        'CUI-RO-20000001','CUI-RO-20000002','CUI-RO-20000003','CUI-RO-20000004','CUI-RO-20000005','CUI-RO-20000006',
                                        'CUI-RO-20000007','CUI-RO-20000008','CUI-RO-20000009','CUI-RO-20000010','CUI-RO-20000011','CUI-RO-20000012'
            )
    ),
    city AS (
        SELECT id, code
        FROM cities
        WHERE code IN (
                       'CJ-NAP','B-001','BV-BV','IS-IS','CT-CT','BH-ORA','DJ-CRV','GL-GL','AR-ARAD','HD-DV','BC-BC','AB-AI',
                       'DB-TGV','BR-BRA','CS-RES','BN-BIS','CV-SG','HR-MC','AG-PIT','BZ-BZ','CL-CL','GJ-TJ','GR-GR','BT-BOT'
            )
    )
INSERT INTO buildings(owner_client_id, city_id, street, number, construction_year, type, floors, surface_area, insured_value)
VALUES
  ((SELECT id FROM c WHERE identification_number='CNP-100000000001'), (SELECT id FROM city WHERE code='CJ-NAP'), 'Observatorului', '10A', 2010, 'RESIDENTIAL', 2, 120.5, 350000),
  ((SELECT id FROM c WHERE identification_number='CNP-100000000002'), (SELECT id FROM city WHERE code='B-001'),  'Victoriei',      '21',  2005, 'RESIDENTIAL', 8, 75.0,  420000),
  ((SELECT id FROM c WHERE identification_number='CNP-100000000003'), (SELECT id FROM city WHERE code='BV-BV'),  'Republicii',     '5',   1998, 'RESIDENTIAL', 3, 98.0,  300000),
  ((SELECT id FROM c WHERE identification_number='CNP-100000000004'), (SELECT id FROM city WHERE code='IS-IS'),  'Copou',          '12',  1975, 'RESIDENTIAL', 4, 88.0,  260000),
  ((SELECT id FROM c WHERE identification_number='CNP-100000000005'), (SELECT id FROM city WHERE code='CT-CT'),  'Tomis',          '33',  2016, 'RESIDENTIAL', 10, 65.0, 340000),
  ((SELECT id FROM c WHERE identification_number='CNP-100000000006'), (SELECT id FROM city WHERE code='BH-ORA'), 'Decebal',        '7',   2001, 'RESIDENTIAL', 5, 82.0,  240000),
  ((SELECT id FROM c WHERE identification_number='CNP-100000000007'), (SELECT id FROM city WHERE code='DJ-CRV'), 'Unirii',         '18',  1990, 'OFFICE',      6, 310.0, 1200000),
  ((SELECT id FROM c WHERE identification_number='CNP-100000000008'), (SELECT id FROM city WHERE code='GL-GL'),  'Brailei',        '9',   1988, 'RESIDENTIAL', 4, 90.0,  210000),
  ((SELECT id FROM c WHERE identification_number='CNP-100000000009'), (SELECT id FROM city WHERE code='AR-ARAD'), 'Independentei', '44',  2008, 'OFFICE',      3, 450.0, 1500000),
  ((SELECT id FROM c WHERE identification_number='CNP-100000000010'), (SELECT id FROM city WHERE code='HD-DV'),  'Mihai Viteazu',  '2',   1970, 'RESIDENTIAL', 2, 110.0, 180000),
  ((SELECT id FROM c WHERE identification_number='CNP-100000000011'), (SELECT id FROM city WHERE code='BC-BC'),  '9 Mai',          '6',   1985, 'RESIDENTIAL', 4, 78.0,  190000),
  ((SELECT id FROM c WHERE identification_number='CNP-100000000012'), (SELECT id FROM city WHERE code='AB-AI'),  'Horea',          '15',  1965, 'RESIDENTIAL', 2, 105.0, 175000),

  ((SELECT id FROM c WHERE identification_number='CUI-RO-20000001'), (SELECT id FROM city WHERE code='B-001'),  'Splaiul Unirii', '100', 2019, 'OFFICE',      12, 2200.0, 9500000),
  ((SELECT id FROM c WHERE identification_number='CUI-RO-20000002'), (SELECT id FROM city WHERE code='CJ-NAP'), 'Calea Dorobantilor', '55', 2015, 'OFFICE',   7, 980.0,  4200000),
  ((SELECT id FROM c WHERE identification_number='CUI-RO-20000003'), (SELECT id FROM city WHERE code='CT-CT'),  'Portului',        '1',  2000, 'INDUSTRIAL',  2, 5400.0, 11000000),
  ((SELECT id FROM c WHERE identification_number='CUI-RO-20000004'), (SELECT id FROM city WHERE code='IS-IS'),  'Nicolina',        '77', 1995, 'OFFICE',      5, 760.0,  2800000),
  ((SELECT id FROM c WHERE identification_number='CUI-RO-20000005'), (SELECT id FROM city WHERE code='DJ-CRV'), 'Industrial Park', '3',  2012, 'INDUSTRIAL',  1, 8000.0, 15000000),
  ((SELECT id FROM c WHERE identification_number='CUI-RO-20000006'), (SELECT id FROM city WHERE code='CS-RES'), 'Victoriei',       '8',  2007, 'OFFICE',      4, 620.0,  2100000),
  ((SELECT id FROM c WHERE identification_number='CUI-RO-20000007'), (SELECT id FROM city WHERE code='DB-TGV'), 'Calea Bucuresti', '9',  2011, 'RESIDENTIAL', 6, 1400.0, 5000000),
  ((SELECT id FROM c WHERE identification_number='CUI-RO-20000008'), (SELECT id FROM city WHERE code='BR-BRA'), 'Industriilor',    '11', 2003, 'INDUSTRIAL',  2, 3200.0, 6800000),
  ((SELECT id FROM c WHERE identification_number='CUI-RO-20000009'), (SELECT id FROM city WHERE code='HR-MC'), 'Harghita',        '20', 2014, 'OFFICE',      3, 410.0,  1600000),
  ((SELECT id FROM c WHERE identification_number='CUI-RO-20000010'), (SELECT id FROM city WHERE code='CV-SG'), 'Oltului',         '14', 2009, 'RESIDENTIAL', 4, 900.0,  3200000),
  ((SELECT id FROM c WHERE identification_number='CUI-RO-20000011'), (SELECT id FROM city WHERE code='GR-GR'), 'Dunarii',         '5',  2006, 'OFFICE',      2, 350.0,  1250000),
  ((SELECT id FROM c WHERE identification_number='CUI-RO-20000012'), (SELECT id FROM city WHERE code='BN-BIS'), 'Uzinei',          '2',  2018, 'INDUSTRIAL',  1, 4600.0, 8900000);

-- ============================================================================
-- 6) CLIENT IDENTIFIER CHANGE AUDIT (24 rows)
-- ============================================================================

-- We simulate that each client had an "old" identifier, and it was changed to the current one.
-- This matches the "track changes" approach allowed by the doc.
INSERT INTO client_identifier_changes(client_id, old_value, new_value, changed_by, changed_at)
SELECT
    cl.id,
    CASE
        WHEN cl.identification_number LIKE 'CNP-%' THEN replace(cl.identification_number, 'CNP-', 'CNP-OLD-')
        WHEN cl.identification_number LIKE 'CUI-%' THEN replace(cl.identification_number, 'CUI-', 'CUI-OLD-')
        ELSE 'OLD-' || cl.identification_number
        END AS old_value,
    cl.identification_number AS new_value,
    'SYSTEM_SEED' AS changed_by,
    now() - interval '10 days' AS changed_at
FROM clients cl
WHERE cl.identification_number IN (
    'CNP-100000000001','CNP-100000000002','CNP-100000000003','CNP-100000000004','CNP-100000000005','CNP-100000000006',
    'CNP-100000000007','CNP-100000000008','CNP-100000000009','CNP-100000000010','CNP-100000000011','CNP-100000000012',
    'CUI-RO-20000001','CUI-RO-20000002','CUI-RO-20000003','CUI-RO-20000004','CUI-RO-20000005','CUI-RO-20000006',
    'CUI-RO-20000007','CUI-RO-20000008','CUI-RO-20000009','CUI-RO-20000010','CUI-RO-20000011','CUI-RO-20000012'
    );
