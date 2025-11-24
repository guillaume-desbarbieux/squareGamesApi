package com.guillaume.squareGamesApi.dao;

import com.guillaume.squareGamesApi.service.GamePlugin;
import fr.le_campus_numerique.square_games.engine.Game;
import fr.le_campus_numerique.square_games.engine.InconsistentGameDefinitionException;
import fr.le_campus_numerique.square_games.engine.TokenPosition;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Repository;

import java.util.*;


@Repository
public class MysqlGameDAO implements GameDAO {
    private final Map<UUID, Game> gameMap;
    private final JdbcTemplate jdbc;

    private final Map<String, GamePlugin> pluginMap;

    public MysqlGameDAO(JdbcTemplate jdbc, List<GamePlugin> plugins) {
        this.gameMap = new HashMap<>();
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
                "SELECT UUID FROM player WHERE gameUuid = ?",
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
            System.out.println(e.getMessage());
            return null;
        }
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
    public Collection<UUID> getGameUUIDs() {
        return jdbc.query(
                "SELECT uuid from game",
                (rs, rowNum) -> UUID.fromString(rs.getString("uuid"))
        );
    }

    public static void main(String[] args) {
        // Configuration de la DataSource
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3307/SquareGames");
        dataSource.setUsername("user");
        dataSource.setPassword("user");



        // Création du JdbcTemplate avec la DataSource
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        // Création du DAO
        MysqlGameDAO dao = new MysqlGameDAO(jdbcTemplate);
        dao.getGameById(UUID.fromString("fab835a0-5b1f-453e-a113-0a80d89b0803"));
    }
}


