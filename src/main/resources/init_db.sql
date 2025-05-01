CREATE DATABASE prom_po;

CREATE SCHEMA IF NOT EXISTS prom_po;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp" SCHEMA prom_po;

CREATE TABLE prom_po.file_index
(
    id            uuid  NOT NULL DEFAULT prom_po.uuid_generate_v4()
        PRIMARY KEY,
    file_path     text  NOT NULL,
    counted_words jsonb NOT NULL
);