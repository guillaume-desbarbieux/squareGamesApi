package com.guillaume.squareGamesApi.service;

import com.guillaume.squareGamesApi.model.GameCreationParams;
import fr.le_campus_numerique.square_games.engine.Game;
import fr.le_campus_numerique.square_games.engine.InconsistentGameDefinitionException;
import fr.le_campus_numerique.square_games.engine.TokenPosition;
import fr.le_campus_numerique.square_games.engine.tictactoe.TicTacToeGameFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class TicTacToePlugin implements GamePlugin {

    private final TicTacToeGameFactory factory;
    private final int defaultPlayerCount;
    private final int defaultBoardSize;
    private final MessageSource messageSource;

    public TicTacToePlugin(TicTacToeGameFactory ticTacToeGameFactory,
                           @Value("${game.tictactoe.default-player-count}") int defaultPlayerCount,
                           @Value("${game.tictactoe.default-board-size}") int defaultBoardSize,
                           MessageSource messageSource) {
        this.factory = ticTacToeGameFactory;
        this.defaultPlayerCount = defaultPlayerCount;
        this.defaultBoardSize = defaultBoardSize;
        this.messageSource = messageSource;
    }

    @Override
    public Game createGame(GameCreationParams params, UUID userId) {

        int playerCount = params.playerCount() == 0 ? defaultPlayerCount : params.playerCount();

        int boardSize = params.boardSize() == 0 ? defaultBoardSize : params.boardSize();

        if (userId == null)
            return factory.createGame(playerCount, boardSize);
        else {
            Set<UUID> players = new HashSet<>();
            players.add(userId);
            for (int i = 1; i < playerCount; i++)
                players.add(UUID.randomUUID());
            return factory.createGame(boardSize, players);
        }
    }

    @Override
    public Game createGameWithIds(UUID gameId, int boardSize, List<UUID> players, Collection<TokenPosition<UUID>> boardTokens, Collection<TokenPosition<UUID>> removedTokens) throws InconsistentGameDefinitionException {
        return factory.createGameWithIds(gameId, boardSize, players, boardTokens, removedTokens);
    }

    @Override
    public String getName(Locale locale) {
        return messageSource.getMessage(
                "game.tictactoe.name",
                new Object[]{"Guillaume", "Géraud"},
                locale
        );
    }

    @Override
    public String getGameIdentifier() {
        return factory.getGameFactoryId();
    }
}
