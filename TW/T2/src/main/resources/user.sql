CREATE TABLE events (
    name VARCHAR(255) PRIMARY KEY NOT NULL,
    description TEXT NOT NULL,
    date TIMESTAMP NOT NULL,
    value REAL NOT NULL DEFAULT 0
);

CREATE TABLE users (
    user_name VARCHAR(255) PRIMARY KEY NOT NULL,
    user_pass VARCHAR(255) NOT NULL,
    "enable" SMALLINT NOT NULL DEFAULT 1
);

CREATE TABLE user_roles (
    user_name VARCHAR(255) NOT NULL,
    user_role VARCHAR(30) NOT NULL,
    FOREIGN KEY (user_name) REFERENCES users (user_name)
);

CREATE TABLE registrations (
    event_name VARCHAR(255) NOT NULL REFERENCES events(name),
    user_name VARCHAR(255) NOT NULL REFERENCES users(user_name),
    participant_name VARCHAR(255) NOT NULL,
    gender CHAR(1) NOT NULL,
    tier VARCHAR(10) NOT NULL,
    payment_reference VARCHAR(255),
    payment_entity VARCHAR(255),
    status VARCHAR(50) DEFAULT 'pending',
    PRIMARY KEY(participant_name,event_name)
);

CREATE TABLE participant_times (
    event_name VARCHAR(255) REFERENCES events(name),
    participant_name VARCHAR(255) NOT NULL,
    participant_number int NOT NULL,
    checkpoint VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    PRIMARY KEY (participant_name, event_name),
    FOREIGN KEY (participant_name,event_name) REFERENCES registrations (participant_name,event_name)
);