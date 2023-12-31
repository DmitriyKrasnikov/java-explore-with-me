DROP TABLE IF EXISTS comments, compilations_events, participation_requests, events, users, compilations, categories CASCADE;

CREATE TABLE categories (
    id	BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(50) NOT NULL,
    CONSTRAINT pk_category PRIMARY KEY (id),
    CONSTRAINT uq_category_name UNIQUE (name)
);

CREATE TABLE compilations (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    pinned BOOLEAN NOT NULL DEFAULT FALSE,
    title VARCHAR(50) NOT NULL,
    CONSTRAINT pk_comp PRIMARY KEY (id),
    CONSTRAINT uq_compilation_name UNIQUE (title)
);


CREATE TABLE users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    email VARCHAR(254) NOT NULL,
    name VARCHAR(250) NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_email UNIQUE (email)
);

CREATE TABLE events (
    id	BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    annotation VARCHAR NOT NULL,
    category_id INTEGER NOT NULL,
    created_on TIMESTAMP NOT NULL,
    description	VARCHAR NOT NULL,
    event_date TIMESTAMP,
    initiator_id BIGINT NOT NULL,
    lat FLOAT NOT NULL,
    lon FLOAT NOT NULL,
    paid BOOLEAN NOT NULL DEFAULT  FALSE,
    participant_limit INTEGER NOT NULL DEFAULT 0,
    published_on TIMESTAMP,
    request_moderation BOOLEAN NOT NULL DEFAULT TRUE,
    state VARCHAR NOT NULL,
    title VARCHAR NOT NULL,
    CONSTRAINT pk_events PRIMARY KEY (id),
    CONSTRAINT fk_events_category_id FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE RESTRICT,
    CONSTRAINT fk_events_initiator_id FOREIGN KEY (initiator_id) REFERENCES users (id) ON DELETE RESTRICT
);

CREATE TABLE participation_requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created TIMESTAMP NOT NULL,
    event_id BIGINT NOT NULL,
    requester_id BIGINT NOT NULL,
    status VARCHAR NOT NULL,
    CONSTRAINT pk_participation_requests PRIMARY KEY (id),
    CONSTRAINT fk_participation_request_event_id FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE,
    CONSTRAINT fk_participation_request_requester_id FOREIGN KEY (requester_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT uq_request UNIQUE (event_id, requester_id)
);

CREATE TABLE compilations_events (
    event_id BIGINT NOT NULL,
    compilation_id BIGINT NOT NULL,
    CONSTRAINT pk_compilations_events PRIMARY KEY (
            event_id,compilation_id
         ),
    CONSTRAINT fk_compilations_events_event_id FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE,
    CONSTRAINT fk_compilations_events_compilation_id FOREIGN KEY (compilation_id) REFERENCES compilations (id) ON DELETE CASCADE
);

CREATE TABLE comments (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    content VARCHAR(1000) NOT NULL,
    user_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    created_on TIMESTAMP NOT NULL,
    CONSTRAINT pk_comments PRIMARY KEY (id),
    CONSTRAINT fk_comments_event_id FOREIGN KEY(event_id) REFERENCES events (id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_user_id FOREIGN KEY(user_id) REFERENCES users (id) ON DELETE CASCADE
);