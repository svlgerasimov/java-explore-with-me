
CREATE TABLE IF NOT EXISTS apps (
    name VARCHAR(256),
    CONSTRAINT pk_apps PRIMARY KEY (name)
);

CREATE TABLE IF NOT EXISTS stats (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    app VARCHAR(256) NOT NULL,
    uri VARCHAR(256) NOT NULL,
    ip VARCHAR(15) NOT NULL,
    hit_timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_stats PRIMARY KEY (id),
    CONSTRAINT fk_stats_app FOREIGN KEY (app) REFERENCES apps(name)
);