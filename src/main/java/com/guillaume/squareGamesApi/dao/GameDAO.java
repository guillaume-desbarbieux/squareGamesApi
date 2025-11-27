package com.guillaume.squareGamesApi.dao;

import fr.le_campus_numerique.square_games.engine.Game;

import java.util.Collection;
import java.util.UUID;

public interface GameDAO {
    boolean existId(UUID gameId);

    Game getGameById(UUID gameId) throws SquareGamesDAOException;

    UUID addGame(Game game) throws SquareGamesDAOException;

    boolean updateGame(Game game) throws SquareGamesDAOException;

    boolean deleteGame(UUID gameId) throws SquareGamesDAOException;

    Collection<UUID> getGameUUIDs(UUID userId);
}
