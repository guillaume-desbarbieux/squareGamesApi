package com.guillaume.squareGamesApi.dao;

import fr.le_campus_numerique.square_games.engine.Game;
import org.springframework.stereotype.Repository;

import java.util.*;


@Repository
public class LocalGameDAO implements GameDAO {

    private final Map<UUID, Game> gameMap;

    public LocalGameDAO() {
        this.gameMap = new HashMap<>();
    }

    @Override
    public boolean existId(UUID gameId) {
        return gameMap.get(gameId) == null;
    }

    @Override
    public Game getGameById(UUID gameId) {
        return gameMap.get(gameId);
    }

    @Override
    public UUID addGame(Game game) {
        if (game == null || game.getId() == null)
            throw new SquareGamesDAOException("Can't save null game");

        if (gameMap.put(game.getId(), game) == null)
            return game.getId();
        else
            throw new SquareGamesDAOException("Problem with Saving Game");
    }

    @Override
    public boolean updateGame(Game game) {
        if (game == null)
            throw new SquareGamesDAOException("Can't update null game");
        return gameMap.replace(game.getId(), game) != null;
    }

    @Override
    public boolean deleteGame(UUID gameId) {
        return gameMap.remove(gameId) != null;
    }

    @Override
    public Collection<UUID> getGameUUIDs(UUID userId) {
        Collection<UUID> uuids = new ArrayList<>();
        for (Game game : gameMap.values())
            if (game.getPlayerIds().contains(userId))
                uuids.add(game.getId());

        return uuids;
    }
}
