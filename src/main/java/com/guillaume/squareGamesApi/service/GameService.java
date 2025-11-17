package com.guillaume.squareGamesApi.service;

import com.guillaume.squareGamesApi.model.GameCreationParams;
import com.guillaume.squareGamesApi.model.GameMoveParam;
import fr.le_campus_numerique.square_games.engine.CellPosition;
import fr.le_campus_numerique.square_games.engine.Game;
import fr.le_campus_numerique.square_games.engine.InvalidPositionException;
import fr.le_campus_numerique.square_games.engine.Token;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface GameService {
    UUID createGame(GameCreationParams params);
    Game getGame(UUID gameId);
    Map<CellPosition, Token> getBoardTokens(UUID gameId);
    Collection<Token> getRemainingTokens(UUID gameId);
    Collection<Token> getRemovedTokens(UUID gameId);
    Set<CellPosition> getAllowedMovesFromRemaining(UUID gameID, String name);
    Set<CellPosition> getAllowedMovesFromBoard(UUID gameID, CellPosition cellPosition);
    void playMove(UUID gameId, GameMoveParam gameMove) throws InvalidPositionException, IllegalArgumentException;
    Collection<Game> getGames();
}
