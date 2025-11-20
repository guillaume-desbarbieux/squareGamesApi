package com.guillaume.squareGamesApi.controller;

import fr.le_campus_numerique.square_games.engine.CellPosition;

import java.util.UUID;

public record TokenDTO(
        java.util.@jakarta.validation.constraints.NotNull Optional<UUID> ownerId,
        String name,
        CellPosition position) {
}
