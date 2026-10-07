CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE products (
                          id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          name         VARCHAR(120) NOT NULL,
                          description  VARCHAR(500),
                          price        NUMERIC(12,2) NOT NULL CHECK (price > 0),
                          category     VARCHAR(40)  NOT NULL,
                          status       VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
                          created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
                          updated_at   TIMESTAMPTZ,
                          deleted      BOOLEAN      NOT NULL DEFAULT false
);

CREATE INDEX idx_products_category_created
    ON products (category, created_at DESC)
    WHERE deleted = false;

CREATE INDEX idx_products_status
    ON products (status)
    WHERE deleted = false;