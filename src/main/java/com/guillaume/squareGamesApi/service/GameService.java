package com.guillaume.squareGamesApi.service;

import com.guillaume.squareGamesApi.model.GameCreationParams;
import com.guillaume.squareGamesApi.model.GameMoveParam;
import fr.le_campus_numerique.square_games.engine.*;

import java.util.*;

public interface GameService {
    Collection<String> getGameIdentifiers();

    UUID createGame(GameCreationParams params, UUID userId) throws IllegalArgumentException, SquareGameUnknownUserException;

    Game getGame(UUID gameId, UUID userId) throws SquareGameUnauthorizedException;

    Map<CellPosition, Token> getBoardTokens(UUID gameId, UUID userId) throws SquareGameUnauthorizedException;

    Collection<Token> getRemainingTokens(UUID gameId, UUID userId) throws SquareGameUnauthorizedException;

    Collection<Token> getRemovedTokens(UUID gameId, UUID userId) throws SquareGameUnauthorizedException;

    Set<CellPosition> getAllowedMovesFromRemaining(UUID gameID, String name, UUID userId) throws SquareGameUnauthorizedException;

    Set<CellPosition> getAllowedMovesFromBoard(UUID gameID, CellPosition cellPosition, UUID userId) throws SquareGameUnauthorizedException;

    void playMove(UUID gameId, GameMoveParam gameMove, UUID userId) throws InvalidPositionException, IllegalArgumentException, SquareGameUnauthorizedException;

    String getGameName(String identifier, Locale locale);

    Map<String, String> getCatalog(Locale locale);

    Boolean deleteGame(UUID gameId, UUID userId) throws SquareGameUnauthorizedException;

    Collection<UUID> getGameUUIDs(UUID userId);

}
