package com.guillaume.squareGamesApi.service;

import com.guillaume.squareGamesApi.model.GameCreationParams;
import fr.le_campus_numerique.square_games.engine.Game;
import fr.le_campus_numerique.square_games.engine.InconsistentGameDefinitionException;
import fr.le_campus_numerique.square_games.engine.TokenPosition;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public interface GamePlugin {
    Game createGame(GameCreationParams params);
    Game createGameWithIds(
            UUID gameId,
            int boardSize,
            List<UUID> players,
            Collection<TokenPosition<UUID>> boardTokens,
            Collection<TokenPosition<UUID>> removedTokens) throws InconsistentGameDefinitionException;
    String getName(Locale locale);
    String getGameIdentifier();
}
