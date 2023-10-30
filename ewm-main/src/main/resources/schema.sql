DROP TABLE IF EXISTS categories, users, locations, events, compilations, requests, compilation_event, comments CASCADE;

CREATE TABLE IF NOT EXISTS categories
(
    id   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(100) UNIQUE                     NOT NULL,
    CONSTRAINT pk_categories PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(250)                            NOT NULL,
    email VARCHAR(254) UNIQUE                     NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS locations
(
    id  BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    lat DECIMAL                                 NOT NULL,
    lon DECIMAL                                 NOT NULL,
    CONSTRAINT pk_locations PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS events
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    annotation         VARCHAR(2000)                           NOT NULL,
    category_id        BIGINT,
    created_on         TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
    description        VARCHAR(7000)                           NOT NULL,
    event_date         TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    initiator_id       BIGINT                                  NOT NULL,
    location_id        BIGINT                                  NOT NULL,
    paid               BOOLEAN                                 NOT NULL,
    participant_limit  BIGINT                                  NOT NULL,
    published_on       TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOLEAN DEFAULT TRUE,
    state              VARCHAR(64)                             NOT NULL,
    title              VARCHAR(120)                            NOT NULL,
    CONSTRAINT pk_events PRIMARY KEY (id),
    CONSTRAINT fk_events_categories FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT fk_events_users FOREIGN KEY (initiator_id) REFERENCES users (id),
    CONSTRAINT fk_events_locations FOREIGN KEY (location_id) REFERENCES locations (id)
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    event_id     BIGINT                                  NOT NULL,
    requester_id BIGINT                                  NOT NULL,
    created_on   TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
    status       VARCHAR(50)                             NOT NULL,
    CONSTRAINT pk_requests PRIMARY KEY (id),
    CONSTRAINT fk_requests_events FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT fk_requests_users FOREIGN KEY (requester_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS compilations
(
    id     BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    pinned BOOLEAN,
    title  VARCHAR(100)                            NOT NULL,
    CONSTRAINT pk_compilations PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS compilation_event
(
    compilation_id BIGINT NOT NULL,
    event_id       BIGINT NOT NULL,
    CONSTRAINT pk_compilation_events_compilations PRIMARY KEY (compilation_id, event_id),
    CONSTRAINT fk_compilation_events_compilations FOREIGN KEY (compilation_id) REFERENCES compilations (id),
    CONSTRAINT fk_compilation_events_events FOREIGN KEY (event_id) REFERENCES events (id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id               BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    event_id         BIGINT                                  NOT NULL,
    author_id        BIGINT                                  NOT NULL,
    reply_to_id      BIGINT,
    answered         BOOLEAN DEFAULT false,
    text             VARCHAR(7000)                           NOT NULL,
    created_on       TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
    last_updated_on  TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
    update_initiator VARCHAR(15),
    CONSTRAINT pk_comments PRIMARY KEY (id),
    CONSTRAINT fk_comments_events FOREIGN KEY (event_id) REFERENCES events(id),
    CONSTRAINT fk_comments_users FOREIGN KEY (author_id) REFERENCES users(id),
    CONSTRAINT fk_comments_comments FOREIGN KEY (reply_to_id) REFERENCES comments(id)
);