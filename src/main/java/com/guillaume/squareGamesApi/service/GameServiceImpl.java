package com.guillaume.squareGamesApi.service;

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
    private final Collection<Game> games;

    public GameServiceImpl(List<GamePlugin> plugins) {
        this.plugins = plugins;
        pluginMap = new HashMap<>();
        games = new ArrayList<>();

        for (GamePlugin plugin : plugins)
            pluginMap.put(plugin.getGamePluginId(), plugin);
    }

    @PostConstruct
    public void initForTest() throws InconsistentGameDefinitionException {
        games.add(plugins.getFirst().createGameWithIds(
                UUID.fromString("fab835a0-5b1f-453e-a113-0a80d89b0803"),
                3,
                List.of(UUID.fromString("6f557748-aee4-417a-bf5e-7972874d060a"), UUID.fromString("0d1fafd2-4ef5-4635-9ad2-13d2545810e6")),
                List.of(),
                List.of()));
    }

    @Override
    public Collection<String> getGameIdentifiers() {
        Collection<String> gameIdentifiers = new ArrayList<>();
        System.out.println(plugins.getFirst().getName(Locale.FRENCH));
        for (GamePlugin plugin : plugins)
            gameIdentifiers.add(plugin.getGamePluginId());
        return gameIdentifiers;
    }

    @Override
    public UUID createGame(GameCreationParams params) throws IllegalArgumentException {
        GamePlugin plugin = pluginMap.get(params.identifier());

        if (plugin == null)
            throw new IllegalArgumentException("Unknown game Identifier : " + params.identifier());

        Game game = plugin.createGame(params);
        games.add(game);
        return game.getId();
    }


    @Override
    public Game getGame(UUID gameId) {
        for (Game game : games)
            if (game.getId().equals(gameId))
                return game;
        return null;
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
        return games;
    }
}