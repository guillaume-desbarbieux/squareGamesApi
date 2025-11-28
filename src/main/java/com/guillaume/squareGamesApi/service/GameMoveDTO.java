package com.guillaume.squareGamesApi.service;

import jakarta.validation.constraints.NotNull;

import java.util.Optional;
import java.util.UUID;

public record GameMoveDTO(@NotNull Optional<String> tokenName,
                          @NotNull Optional<CellPositionDTO> fromCell,
                          @NotNull CellPositionDTO toCell,
                          @NotNull UUID gameId,
                          @NotNull UUID playerId
) {
}