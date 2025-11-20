package com.guillaume.squareGamesApi.service;

import com.guillaume.squareGamesApi.dao.GameDAO;
import com.guillaume.squareGamesApi.model.GameCreationParams;
import com.guillaume.squareGamesApi.model.GameMoveParam;
import fr.le_campus_numerique.square_games.engine.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameServiceImpl implements GameService {
    private final List<GamePlugin> plugins;
    private final Map<String, GamePlugin> pluginMap;
    private final GameDAO gameDAO;

    public GameServiceImpl(List<GamePlugin> plugins, GameDAO gameDAO) {
        this.gameDAO = gameDAO;
        this.plugins = plugins;
        pluginMap = new HashMap<>();

        for (GamePlugin plugin : plugins)
            pluginMap.put(plugin.getGameIdentifier(), plugin);
    }

    @PostConstruct
    public void initForTest() throws InconsistentGameDefinitionException {
        Game gameForTest = pluginMap.get("tictactoe").createGameWithIds(
                UUID.fromString("fab835a0-5b1f-453e-a113-0a80d89b0803"),
                3,
                List.of(UUID.fromString("6f557748-aee4-417a-bf5e-7972874d060a"), UUID.fromString("0d1fafd2-4ef5-4635-9ad2-13d2545810e6")),
                List.of(),
                List.of());

        gameDAO.addGame(gameForTest);
    }

    @Override
    public Collection<String> getGameIdentifiers() {
        Collection<String> gameIdentifiers = new ArrayList<>();
        for (GamePlugin plugin : plugins)
            gameIdentifiers.add(plugin.getGameIdentifier());
        return gameIdentifiers;
    }

    @Override
    public UUID createGame(GameCreationParams params) throws IllegalArgumentException {
        GamePlugin plugin = pluginMap.get(params.identifier());

        if (plugin == null)
            throw new IllegalArgumentException("Unknown game Identifier : " + params.identifier());
        Game game = plugin.createGame(params);
        return gameDAO.addGame(game);
    }


    @Override
    public Game getGame(UUID gameId) {
       return gameDAO.getGameById(gameId);
    }

    @Override
    public Map<CellPosition, Token> getBoardTokens(UUID gameId) {
        Game game = getGame(gameId);
        if (game == null)
            return Map.of();

        return game.getBoard();
    }

    @Override
    public Collection<Token> getRemainingTokens(UUID gameId) {
        Game game = getGame(gameId);
        if (game == null)
            return List.of();

        return game.getRemainingTokens();
    }

    @Override
    public Collection<Token> getRemovedTokens(UUID gameId) {
        Game game = getGame(gameId);
        if (game == null)
            return List.of();

        return game.getRemovedTokens();
    }

    @Override
    public Set<CellPosition> getAllowedMovesFromRemaining(UUID gameId, String name) {
        Collection<Token> tokens = getRemainingTokens(gameId);

        for (Token token : tokens)
            if (Objects.equals(token.getName(), name))
                return token.getAllowedMoves();
        return Set.of();
    }

    @Override
    public Set<CellPosition> getAllowedMovesFromBoard(UUID gameID, CellPosition cellPosition) {
        Map<CellPosition, Token> tokens = getBoardTokens(gameID);

        if (tokens == null)
            return Set.of();

        return tokens.get(cellPosition).getAllowedMoves();
    }

    @Override
    public void playMove(UUID gameId, GameMoveParam gameMove) throws InvalidPositionException, IllegalArgumentException {
        Game game = getGame(gameId);
        if (game == null)
            return;

        if (gameMove.fromCell() != null) {
            Token token = getBoardTokens(gameId).get(gameMove.fromCell());
            if (token == null)
                throw new IllegalArgumentException("No token on the board at this position");
            else {
                token.moveTo(gameMove.toCell());
                return;
            }
        }

        if (gameMove.name() != null) {
            Collection<Token> tokens = getRemainingTokens(gameId);
            for (Token token : tokens)
                if (Objects.equals(token.getName(), gameMove.name())) {
                    token.moveTo(gameMove.toCell());
                    return;
                }
            throw new IllegalArgumentException("No tokens remaining with this name");
        }

        throw new IllegalArgumentException("We can't identify the token you choose");
    }

    @Override
    public Collection<Game> getGames() {
        return gameDAO.getGames();
    }

    @Override
    public String getGameName(String identifier, Locale locale) {
        GamePlugin gamePlugin = pluginMap.get(identifier);
        return gamePlugin.getName(locale);
    }

    @Override
    public Map<String, String> getCatalog(Locale locale) {
        Collection<String> gameIdentifiers = getGameIdentifiers();
        if (gameIdentifiers.isEmpty())
            return Map.of();

        Map<String, String> catalog = new HashMap<>();

        for (String identifier : gameIdentifiers)
            catalog.put(identifier, getGameName(identifier, locale));

        return catalog;
    }

    @Override
    public Boolean deleteGame(UUID gameId) {
        return gameDAO.deleteGame(gameId);
    }

    @Override
    public Collection<UUID> getGameUUIDs() {
        return gameDAO.getGameUUIDs();
    }
}