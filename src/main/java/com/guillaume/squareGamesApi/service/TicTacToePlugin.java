package com.guillaume.squareGamesApi.service;

import com.guillaume.squareGamesApi.model.GameCreationParams;
import fr.le_campus_numerique.square_games.engine.Game;
import fr.le_campus_numerique.square_games.engine.InconsistentGameDefinitionException;
import fr.le_campus_numerique.square_games.engine.TokenPosition;
import fr.le_campus_numerique.square_games.engine.tictactoe.TicTacToeGameFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Component
public class TicTacToePlugin implements GamePlugin {

    private final TicTacToeGameFactory ticTacToeGameFactory;
    private final int defaultPlayerCount;
    private final int defaultBoardSize;
    private final MessageSource messageSource;

    public TicTacToePlugin(TicTacToeGameFactory ticTacToeGameFactory,
                           @Value("${game.tictactoe.default-player-count}") int defaultPlayerCount,
                           @Value("${game.tictactoe.default-board-size}") int defaultBoardSize,
                           MessageSource messageSource, MessageSource messageSource1) {
        this.ticTacToeGameFactory = ticTacToeGameFactory;
        this.defaultPlayerCount = defaultPlayerCount;
        this.defaultBoardSize = defaultBoardSize;
        this.messageSource = messageSource1;
    }

    @Override
    public Game createGame(GameCreationParams params) {
        int playerCount = params.playerCount() == 0 ? defaultPlayerCount : params.playerCount();
        int boardSize = params.boardSize() == 0 ? defaultBoardSize : params.boardSize();
        return ticTacToeGameFactory.createGame(playerCount, boardSize);
    }

    @Override
    public Game createGameWithIds(UUID gameId, int boardSize, List<UUID> players, Collection<TokenPosition<UUID>> boardTokens, Collection<TokenPosition<UUID>> removedTokens) throws InconsistentGameDefinitionException {
        return ticTacToeGameFactory.createGameWithIds(gameId, boardSize, players, boardTokens, removedTokens);
    }

    @Override
    public String getName(Locale language) {
        return "";
//        return messageSource.getMessage();
    }

    @Override
    public String getGamePluginId() {
        return ticTacToeGameFactory.getGameFactoryId();
    }
}
