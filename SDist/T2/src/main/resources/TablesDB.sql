CREATE TABLE artists(
    artistID SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    typeArt VARCHAR(255) NOT NULL,
    locationLatitude VARCHAR(255) NOT NULL,
    locationLongitude VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL
);


CREATE TABLE performances(
    performanceID SERIAL PRIMARY KEY,
    artistID INT,
    date DATE,
    locationLatitude VARCHAR(255) NOT NULL,
    locationLongitude VARCHAR(255) NOT NULL,
    FOREIGN KEY (artistID) REFERENCES Artists
);

CREATE TABLE users (
    userID SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(15) NOT NULL,
    status VARCHAR(20) NOT NULL
);


CREATE TABLE donations(
    donationID SERIAL PRIMARY KEY,
    userID INT NOT NULL,
    artistID INT NOT NULL,
    value INT NOT NULL,
    date DATE NOT NULL,
    FOREIGN KEY(artistID) REFERENCES Artists,
    FOREIGN KEY(userID) REFERENCES users
);

CREATE  TABLE ratings(
    ratingID SERIAL PRIMARY KEY,
    artistID INT NOT NULL,
    rating INT NOT NULL,
    FOREIGN KEY(artistID) REFERENCES Artists
);