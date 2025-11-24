CREATE TABLE IF NOT EXISTS game
(
    uuid      VARCHAR(255) PRIMARY KEY,
    boardSize INT          NOT NULL,
    gameType  VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS player
(
    uuid     VARCHAR(255) NOT NULL,
    gameUuid VARCHAR(255) NOT NULL,
    PRIMARY KEY (uuid, gameUuid),
    FOREIGN KEY (gameUuid) REFERENCES game (uuid)
);

CREATE TABLE IF NOT EXISTS boardToken
(
    uuid       VARCHAR(255) PRIMARY KEY,
    gameUuid   VARCHAR(255) NOT NULL,
    playerUuid VARCHAR(255) NOT NULL,
    x          INT          NOT NULL,
    y          INT          NOT NULL,
    FOREIGN KEY (gameUuid) REFERENCES game (uuid),
    FOREIGN KEY (playerUuid) REFERENCES player (uuid)
);

CREATE TABLE IF NOT EXISTS removedToken
(
    uuid       VARCHAR(255) PRIMARY KEY,
    gameUuid   VARCHAR(255) NOT NULL,
    playerUuid VARCHAR(255) NOT NULL,
    name       VARCHAR(255) NOT NULL,
    FOREIGN KEY (gameUuid) REFERENCES game (uuid),
    FOREIGN KEY (playerUuid) REFERENCES player (uuid)
);

