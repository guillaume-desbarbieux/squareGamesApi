package com.guillaume.squareGamesApi.service;

import com.guillaume.squareGamesApi.model.GameCreationParams;
import fr.le_campus_numerique.square_games.engine.Game;
import fr.le_campus_numerique.square_games.engine.InconsistentGameDefinitionException;
import fr.le_campus_numerique.square_games.engine.TokenPosition;
import fr.le_campus_numerique.square_games.engine.taquin.TaquinGameFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Component
public class TaquinPlugin implements GamePlugin {

    private final TaquinGameFactory factory;
    private final int defaultPlayerCount;
    private final int defaultBoardSize;
    private final MessageSource messageSource;

    public TaquinPlugin(TaquinGameFactory factory,
                        @Value("${game.taquin.default-player-count}") int defaultPlayerCount,
                        @Value("${game.taquin.default-board-size}") int defaultBoardSize,
                        MessageSource messageSource) {
        this.factory = factory;
        this.defaultPlayerCount = defaultPlayerCount;
        this.defaultBoardSize = defaultBoardSize;
        this.messageSource = messageSource;
    }

    @Override
    public Game createGame(GameCreationParams params) {

        int playerCount = params.playerCount() == 0 ? defaultPlayerCount : params.playerCount();

        int boardSize = params.boardSize() == 0 ? defaultBoardSize : params.boardSize();
        return factory.createGame(playerCount, boardSize);
    }

    @Override
    public Game createGameWithIds(UUID gameId, int boardSize, List<UUID> players, Collection<TokenPosition<UUID>> boardTokens, Collection<TokenPosition<UUID>> removedTokens) throws InconsistentGameDefinitionException {
        return factory.createGameWithIds(gameId, boardSize, players, boardTokens, removedTokens);
    }

    @Override
    public String getName(Locale locale) {
        return messageSource.getMessage(
                "game.taquin.name",
                null,
                locale
        );
    }

    @Override
    public String getGameIdentifier() {
        return factory.getGameFactoryId();
    }
}
