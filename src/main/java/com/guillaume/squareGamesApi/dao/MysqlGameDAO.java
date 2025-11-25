package com.guillaume.squareGamesApi.dao;

import com.guillaume.squareGamesApi.service.GamePlugin;
import fr.le_campus_numerique.square_games.engine.Game;
import fr.le_campus_numerique.square_games.engine.InconsistentGameDefinitionException;
import fr.le_campus_numerique.square_games.engine.Token;
import fr.le_campus_numerique.square_games.engine.TokenPosition;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;


@Repository
@Primary
public class MysqlGameDAO implements GameDAO {
    private final JdbcTemplate jdbc;
    private final Map<String, GamePlugin> pluginMap;

    public MysqlGameDAO(JdbcTemplate jdbc, List<GamePlugin> plugins) {
        this.jdbc = jdbc;

        pluginMap = new HashMap<>();
        for (GamePlugin plugin : plugins)
            pluginMap.put(plugin.getGameIdentifier(), plugin);
    }

    @Override
    public boolean existId(UUID gameId) {
        return getGameById(gameId) != null;
    }

    @Override
    public Collection<Game> getGames() {
        Collection<Game> games = new ArrayList<>();
        for (UUID id : getGameUUIDs())
            games.add(getGameById(id));
        return games;
    }

    @Override
    public Game getGameById(UUID gameId) {
        String gameType = jdbc.queryForObject(
                "SELECT gameType FROM game WHERE uuid = ?",
                String.class,
                gameId.toString());

        Integer boardSize = jdbc.queryForObject(
                "SELECT boardSize FROM game WHERE uuid = ?",
                Integer.class,
                gameId.toString());

        GamePlugin plugin = pluginMap.get(gameType);

        if (plugin == null || boardSize == null)
            return null;

        List<UUID> players = jdbc.query(
                "SELECT UUID FROM playerGame WHERE gameUuid = ?",
                (rs, rowNum) -> UUID.fromString(rs.getString("uuid")),
                gameId.toString());

        Collection<TokenPosition<UUID>> boardTokens = jdbc.query(
                "SELECT * FROM boardToken WHERE gameUuid = ? ",
                (rs, rowNum) -> new TokenPosition<>(
                        UUID.fromString(rs.getString("playerUuid")),
                        rs.getString("name"),
                        rs.getInt("x"),
                        rs.getInt("y")
                ),
                gameId.toString());

        Collection<TokenPosition<UUID>> removedTokens = jdbc.query(
                "SELECT * FROM removedToken WHERE gameUuid = ? ",
                (rs, rowNum) -> new TokenPosition<>(
                        UUID.fromString(rs.getString("playerUuid")),
                        rs.getString("name"),
                        rs.getInt("x"),
                        rs.getInt("y")
                ),
                gameId.toString());

        try {
            return plugin.createGameWithIds(gameId, boardSize, players, boardTokens, removedTokens);
        } catch (InconsistentGameDefinitionException e) {
            System.out.println("Error when creating : " + e.getMessage());
            return null;
        }
    }

    @Override
    public UUID addGame(Game game) {

        System.out.println("adding game in BDD");
        System.out.println(game.getBoard());
        if (game == null || game.getId() == null)
            throw new SquareGamesDAOException("Can't save null game");

        String gameId = game.getId().toString();
        try {

            jdbc.update("INSERT INTO game (uuid, boardSize, gameType) VALUES (?, ?, ?)",
                    gameId,
                    game.getBoardSize(),
                    game.getFactoryId());
            for (UUID playerId : game.getPlayerIds()) {
                jdbc.update("INSERT INTO playerGame VALUES (?,?)",
                        playerId.toString(),
                        gameId);
            }
            for (Token token : game.getBoard().values())
                jdbc.update("INSERT INTO boardToken (gameUuid, playerUuid, name, x, y) VALUES (?, ?, ?, ?, ?)",
                        gameId,
                        token.getOwnerId().orElseThrow().toString(),
                        token.getName(),
                        token.getPosition().x(),
                        token.getPosition().y());
            for (Token token : game.getRemovedTokens())
                jdbc.update("INSERT INTO removedToken (gameUuid, playerUuid, name, x, y) VALUES (?, ?, ?, ?, ?)",
                        gameId,
                        token.getOwnerId().orElseThrow().toString(),
                        token.getName(),
                        token.getPosition().x(),
                        token.getPosition().y());

            return game.getId();

        } catch (DataAccessException e) {
            System.out.println("Error when saving : " + e.getMessage());
            return null;
        }
    }


    @Override
    public boolean updateGame(Game game) {
        try {
            if (deleteGame(game.getId()))
                return addGame(game) != null;
            else
                return false;
        } catch (SquareGamesDAOException | DataAccessException e) {
            return false;
        }
    }

    @Override
    public boolean deleteGame(UUID gameId) {
        try {
            jdbc.update("DELETE FROM game where uuid=?", gameId.toString());
            jdbc.update("DELETE FROM playerGame where gameUuid=?", gameId.toString());
            jdbc.update("DELETE FROM boardToken where gameUuid=?", gameId.toString());
            jdbc.update("DELETE FROM removedToken where gameUuid=?", gameId.toString());
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    }

    @Override
    public Collection<UUID> getGameUUIDs() {
        return jdbc.query(
                "SELECT uuid from game",
                (rs, rowNum) -> UUID.fromString(rs.getString("uuid"))
        );
    }
}