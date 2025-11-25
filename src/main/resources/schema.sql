CREATE TABLE IF NOT EXISTS game
(
    uuid      VARCHAR(255) PRIMARY KEY,
    boardSize INT          NOT NULL,
    gameType  VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS playerGame
(
    uuid     VARCHAR(255) NOT NULL,
    gameUuid VARCHAR(255) NOT NULL,
    PRIMARY KEY (uuid, gameUuid),
    FOREIGN KEY (gameUuid) REFERENCES game (uuid) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS boardToken
(
    id         INT PRIMARY KEY AUTO_INCREMENT,
    gameUuid   VARCHAR(255) NOT NULL,
    playerUuid VARCHAR(255) NOT NULL,
    name       VARCHAR(255) NOT NULL,
    x          INT          NOT NULL,
    y          INT          NOT NULL,
    FOREIGN KEY (gameUuid) REFERENCES game (uuid) ON DELETE CASCADE,
    FOREIGN KEY (playerUuid, gameUuid) REFERENCES playerGame (uuid, gameUuid) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS removedToken
(
    id         INT PRIMARY KEY AUTO_INCREMENT,
    gameUuid   VARCHAR(255) NOT NULL,
    playerUuid VARCHAR(255) NOT NULL,
    name       VARCHAR(255) NOT NULL,
    x          INT          NOT NULL,
    y          INT          NOT NULL,
    FOREIGN KEY (gameUuid) REFERENCES game (uuid) ON DELETE CASCADE,
    FOREIGN KEY (playerUuid, gameUuid) REFERENCES playerGame (uuid, gameUuid) ON DELETE CASCADE
);