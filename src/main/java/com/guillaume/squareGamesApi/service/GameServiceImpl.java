package com.guillaume.squareGamesApi.service;

import com.guillaume.squareGamesApi.dao.*;
import com.guillaume.squareGamesApi.model.*;
import fr.le_campus_numerique.square_games.engine.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.sql.SQLOutput;
import java.util.*;

@Service
public class GameServiceImpl implements GameService {
    private final Map<String, GamePlugin> pluginMap;
    private final Collection<String> gameIdentifiers;
    private final GameDAO gameDAO;

    public GameServiceImpl(List<GamePlugin> plugins, GameDAO gameDAO) {
        this.gameDAO = gameDAO;

        pluginMap = new HashMap<>();
        gameIdentifiers = new ArrayList<>();

        for (GamePlugin plugin : plugins) {
            gameIdentifiers.add(plugin.getGameIdentifier());
            pluginMap.put(plugin.getGameIdentifier(), plugin);
        }
    }
/*
    @PostConstruct
    public void initForTest() throws InconsistentGameDefinitionException {
        GamePlugin plugin = pluginMap.get("tictactoe");

        Game gameForTest = plugin.createGameWithIds(
                UUID.fromString("fab835a0-5b1f-453e-a113-0a80d89b0803"),
                3,
                List.of(UUID.fromString("6f557748-aee4-417a-bf5e-7972874d060a"),
                        UUID.fromString("0d1fafd2-4ef5-4635-9ad2-13d2545810e6")),
                List.of(),
                List.of());

        gameDAO.addGame(gameForTest);
    }

 */

    @Override
    public Collection<String> getGameIdentifiers() {
        return gameIdentifiers;
    }

    @Override
    public UUID createGame(GameCreationParams params, UUID userId) throws IllegalArgumentException, SquareGameUnknownUserException {

        if (!isPlayerRegistered(userId))
            throw new SquareGameUnknownUserException();


        GamePlugin plugin = pluginMap.get(params.identifier());

        if (plugin == null)
            throw new IllegalArgumentException("Unknown game Identifier : " + params.identifier());
        Game game = plugin.createGame(params, userId);
        return gameDAO.addGame(game);
    }

    private boolean isPlayerRegistered(UUID userId) {

        RestClient restClient = RestClient.create();

        ResponseEntity<UserDTO> response = restClient.get()
                .uri("http://localhost:8282/users/" + userId)
                .retrieve()
                .toEntity(UserDTO.class);

        return response.getStatusCode().isSameCodeAs(HttpStatus.OK);
    }


    @Override
    public Game getGame(UUID gameId, UUID playerId) throws SquareGamesDAOException {
        Game game = gameDAO.getGameById(gameId);
        if (game == null)
            return null;
        if (game.getPlayerIds().contains(playerId))
            return game;
        else
            throw new SquareGameUnauthorizedException("");
    }

    @Override
    public Map<CellPosition, Token> getBoardTokens(UUID gameId, UUID userId) throws SquareGamesDAOException {
        Game game = getGame(gameId, userId);
        if (game == null)
            return Map.of();

        return game.getBoard();
    }

    @Override
    public Collection<Token> getRemainingTokens(UUID gameId, UUID userId) {
        Game game = getGame(gameId, userId);
        if (game == null)
            return List.of();

        return game.getRemainingTokens();
    }

    @Override
    public Collection<Token> getRemovedTokens(UUID gameId, UUID userId) {
        Game game = getGame(gameId, userId);
        if (game == null)
            return List.of();

        return game.getRemovedTokens();
    }

    @Override
    public Set<CellPosition> getAllowedMovesFromRemaining(UUID gameId, String name, UUID userId) {
        Collection<Token> tokens = getRemainingTokens(gameId, userId);

        for (Token token : tokens)
            if (Objects.equals(token.getName(), name))
                return token.getAllowedMoves();
        return Set.of();
    }

    @Override
    public Set<CellPosition> getAllowedMovesFromBoard(UUID gameID, CellPosition cellPosition, UUID userId) {
        Map<CellPosition, Token> tokens = getBoardTokens(gameID, userId);

        if (tokens == null)
            return Set.of();

        return tokens.get(cellPosition).getAllowedMoves();
    }

    @Override
    public void playMove(UUID gameId, GameMoveParam gameMove, UUID userId) throws InvalidPositionException, IllegalArgumentException {
        Game game = getGame(gameId, userId);
        if (game == null)
            return;

        if (gameMove.fromCell() != null) {
            Token token = getBoardTokens(gameId, userId).get(gameMove.fromCell());
            if (token == null)
                throw new IllegalArgumentException("No token on the board at this position");
            else {
                token.moveTo(gameMove.toCell());
                gameDAO.updateGame(game);
                return;
            }
        }

        if (gameMove.name() != null) {
            Collection<Token> tokens = game.getRemainingTokens();
            for (Token token : tokens)
                if (Objects.equals(token.getName(), gameMove.name())) {
                    token.moveTo(gameMove.toCell());
                    gameDAO.updateGame(game);
                    return;
                }
            throw new IllegalArgumentException("No tokens remaining with this name");
        }

        throw new IllegalArgumentException("We can't identify the token you choose");
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
    public Boolean deleteGame(UUID gameId, UUID userId) {
        Game game = getGame(gameId, userId);
        return gameDAO.deleteGame(gameId);
    }

    @Override
    public Collection<UUID> getGameUUIDs(UUID userId) {
        return gameDAO.getGameUUIDs(userId);
    }

}