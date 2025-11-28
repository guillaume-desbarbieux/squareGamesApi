package com.guillaume.squareGamesApi.service;

import fr.le_campus_numerique.square_games.engine.GameStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public record GameStatDTO(@NotNull UUID gameId,
                          @NotBlank String gameType,
                          @NotNull int boardSize,
                          @NotNull GameStatus gameStatus,
                          Optional<Collection<GameMoveDTO>> movesHistory,
                          @NotNull Set<UUID> playerIds,
                          Optional<UUID> winnerId
) {
}