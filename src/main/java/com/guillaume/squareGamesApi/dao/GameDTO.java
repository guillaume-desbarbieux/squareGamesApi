package com.guillaume.squareGamesApi.dao;

import fr.le_campus_numerique.square_games.engine.CellPosition;
import fr.le_campus_numerique.square_games.engine.GameStatus;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public record GameDTO(
        UUID gameId,
        int boardSize,
        GameStatus status,
        Map<CellPosition, TokenDTO> board,
        Collection<TokenDTO> remainingTokens,
        Collection<TokenDTO> removedTokens,
        Set<UUID> playerIds) {
}
