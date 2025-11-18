package com.guillaume.squareGamesApi.service;

import com.guillaume.squareGamesApi.model.GameCreationParams;
import fr.le_campus_numerique.square_games.engine.Game;
import fr.le_campus_numerique.square_games.engine.InconsistentGameDefinitionException;
import fr.le_campus_numerique.square_games.engine.TokenPosition;
import fr.le_campus_numerique.square_games.engine.tictactoe.TicTacToeGameFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Component
public class TicTacToePlugin implements GamePlugin {

    private final TicTacToeGameFactory ticTacToeGameFactory;
    int defaultPlayerCount;

    public TicTacToePlugin(TicTacToeGameFactory ticTacToeGameFactory, @Value("${game.tictactoe.default-player-count}") int defaultPlayerCount) {
        this.ticTacToeGameFactory = ticTacToeGameFactory;
        this.defaultPlayerCount = defaultPlayerCount;
    }

    @Override
    public Game createGame(GameCreationParams params) {
        return ticTacToeGameFactory.createGame(defaultPlayerCount, params.boardSize());
    }

    @Override
    public Game createGameWithIds(UUID gameId, int boardSize, List<UUID> players, Collection<TokenPosition<UUID>> boardTokens, Collection<TokenPosition<UUID>> removedTokens) throws InconsistentGameDefinitionException {
        return ticTacToeGameFactory.createGameWithIds(gameId, boardSize, players, boardTokens, removedTokens);
    }

    @Override
    public String getName(Locale language) {
        return "yy";
    }

    @Override
    public String getGamePluginId() {
        return ticTacToeGameFactory.getGameFactoryId();
    }
}
