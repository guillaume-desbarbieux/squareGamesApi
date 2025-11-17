package com.guillaume.squareGamesApi.model;

import fr.le_campus_numerique.square_games.engine.CellPosition;

public record GameMoveParam(String name, CellPosition fromCell, CellPosition toCell) {
}
