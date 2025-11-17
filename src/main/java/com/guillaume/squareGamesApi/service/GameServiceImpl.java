package com.guillaume.squareGamesApi.service;

import com.guillaume.squareGamesApi.model.GameCreationParams;
import com.guillaume.squareGamesApi.model.GameMoveParam;
import fr.le_campus_numerique.square_games.engine.CellPosition;
import fr.le_campus_numerique.square_games.engine.Game;
import fr.le_campus_numerique.square_games.engine.Token;
import fr.le_campus_numerique.square_games.engine.connectfour.ConnectFourGameFactory;
import fr.le_campus_numerique.square_games.engine.taquin.TaquinGameFactory;
import fr.le_campus_numerique.square_games.engine.tictactoe.TicTacToeGameFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameServiceImpl implements GameService {

    @Autowired
    TicTacToeGameFactory ticTacToeGameFactory;
    @Autowired
    ConnectFourGameFactory connectFourGameFactory;
    @Autowired
    TaquinGameFactory taquinGameFactory;

    Collection<Game> games;

    public GameServiceImpl() {
        games = new ArrayList<>();
    }


    @Override
    public UUID createGame(GameCreationParams params) {
        Game game = switch (params.identifier()) {
            case "tictactoe" -> ticTacToeGameFactory.createGame(params.playerCount(), params.boardSize());
            case "connect4" -> connectFourGameFactory.createGame(params.playerCount(), params.boardSize());
            case "15 puzzle" -> taquinGameFactory.createGame(params.playerCount(), params.boardSize());
            default -> null;
        };

        if (game == null)
            return null;

        games.add(game);
        return game.getId();
    }

    @Override
    public Game getGame(UUID gameId) {
        for (Game game : games)
            if (Objects.equals(game.getId().toString(), gameId.toString()))
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
    public Boolean playMove(UUID gameId, GameMoveParam gameMove) {
        return false;
    }

    @Override
    public Collection<Game> getGames() {
        return games;
    }
}
