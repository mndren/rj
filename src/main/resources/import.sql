BEGIN;

-- ===============================
-- CLIENTI
-- ===============================
CREATE TABLE IF NOT EXISTS clienti (
                                       id              BIGSERIAL PRIMARY KEY,
                                       ragione_sociale VARCHAR(255) NOT NULL,
    partita_iva     VARCHAR(20) UNIQUE,
    codice_fiscale  VARCHAR(20),
    email           VARCHAR(255),
    telefono        VARCHAR(50),
    indirizzo       TEXT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

CREATE INDEX IF NOT EXISTS idx_clienti_piva ON clienti(partita_iva);

-- ===============================
-- PRODOTTI
-- ===============================
CREATE TABLE IF NOT EXISTS prodotti (
                                        id          BIGSERIAL PRIMARY KEY,
                                        codice      VARCHAR(50) NOT NULL UNIQUE,
    nome        VARCHAR(255) NOT NULL,
    descrizione TEXT,
    prezzo      NUMERIC(12,2) NOT NULL CHECK (prezzo >= 0),
    iva         NUMERIC(5,2) NOT NULL DEFAULT 22.00,
    attivo      BOOLEAN DEFAULT TRUE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

CREATE INDEX IF NOT EXISTS idx_prodotti_codice ON prodotti(codice);

-- ===============================
-- ORDINI
-- ===============================
CREATE TABLE IF NOT EXISTS ordini (
                                      id          BIGSERIAL PRIMARY KEY,
                                      cliente_id  BIGINT NOT NULL,
                                      data_ordine TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      stato       VARCHAR(50) DEFAULT 'CREATO',
    totale      NUMERIC(12,2) DEFAULT 0,
    CONSTRAINT fk_ordini_cliente
    FOREIGN KEY (cliente_id)
    REFERENCES clienti(id)
    ON DELETE RESTRICT
    );

CREATE INDEX IF NOT EXISTS idx_ordini_cliente ON ordini(cliente_id);

-- ===============================
-- RIGHE ORDINE
-- ===============================
CREATE TABLE IF NOT EXISTS righe_ordine (
                                            id              BIGSERIAL PRIMARY KEY,
                                            ordine_id       BIGINT NOT NULL,
                                            prodotto_id     BIGINT NOT NULL,
                                            quantita        INTEGER NOT NULL CHECK (quantita > 0),
    prezzo_unitario NUMERIC(12,2) NOT NULL,
    iva             NUMERIC(5,2) NOT NULL,
    totale_riga     NUMERIC(12,2) NOT NULL,
    CONSTRAINT fk_righe_ordine
    FOREIGN KEY (ordine_id)
    REFERENCES ordini(id)
    ON DELETE CASCADE,
    CONSTRAINT fk_righe_prodotto
    FOREIGN KEY (prodotto_id)
    REFERENCES prodotti(id)
    );

CREATE INDEX IF NOT EXISTS idx_righe_ordine ON righe_ordine(ordine_id);

-- ===============================
-- FATTURE
-- ===============================
CREATE TABLE IF NOT EXISTS fatture (
                                       id              BIGSERIAL PRIMARY KEY,
                                       ordine_id       BIGINT UNIQUE NOT NULL,
                                       numero_fattura  VARCHAR(50) UNIQUE NOT NULL,
    data_fattura    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    imponibile      NUMERIC(12,2) NOT NULL,
    iva             NUMERIC(12,2) NOT NULL,
    totale          NUMERIC(12,2) NOT NULL,
    CONSTRAINT fk_fattura_ordine
    FOREIGN KEY (ordine_id)
    REFERENCES ordini(id)
    ON DELETE RESTRICT
    );

-- ===============================
-- UTENTI
-- ===============================
CREATE TABLE IF NOT EXISTS utenti (
                                      id              BIGSERIAL PRIMARY KEY,
                                      username        VARCHAR(100) UNIQUE NOT NULL,
    password_hash   TEXT NOT NULL,
    ruolo           VARCHAR(50) NOT NULL,
    attivo          BOOLEAN DEFAULT TRUE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- ===============================
-- DATI REALISTICI MASSIVI
-- ===============================

-- CLIENTI (50)
INSERT INTO clienti (ragione_sociale, partita_iva, email)
SELECT
    'Cliente ' || gs,
    LPAD(gs::text, 11, '0'),
    'cliente' || gs || '@mail.it'
FROM generate_series(1,50) gs
    ON CONFLICT DO NOTHING;

-- PRODOTTI (100)
INSERT INTO prodotti (codice, nome, prezzo)
SELECT
    'PRD' || gs,
    'Prodotto ' || gs,
    ROUND((random() * 500 + 10)::numeric, 2)
FROM generate_series(1,100) gs
    ON CONFLICT DO NOTHING;

-- ORDINI (200)
INSERT INTO ordini (cliente_id, stato)
SELECT
    (random() * 49 + 1)::int,
    CASE
        WHEN random() < 0.5 THEN 'CREATO'
        WHEN random() < 0.8 THEN 'CONFERMATO'
        ELSE 'FATTURATO'
        END
FROM generate_series(1,200);

COMMIT;