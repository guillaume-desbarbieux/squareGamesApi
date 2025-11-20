CREATE TABLE IF NOT EXISTS game
(
    id            VARCHAR(200) PRIMARY KEY,
    boardSize     INT          NOT NULL,
    players       VARCHAR(200) NOT NULL,
    boardTokens   VARCHAR(200) NOT NULL,
    removedTokens VARCHAR(200) NOT NULL
);