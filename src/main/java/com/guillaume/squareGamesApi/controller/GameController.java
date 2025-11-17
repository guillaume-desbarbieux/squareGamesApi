package com.guillaume.squareGamesApi.controller;

import com.guillaume.squareGamesApi.model.GameCreationParams;
import com.guillaume.squareGamesApi.model.GameMoveParam;
import fr.le_campus_numerique.square_games.engine.CellPosition;
import fr.le_campus_numerique.square_games.engine.Token;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/games")
public class GameController {

    @PostMapping
    public ResponseEntity<String> createGame(@RequestBody GameCreationParams params) {
        if (params.identifier() == null || params.playerCount() == 0 || params.boardSize() == 0)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Champ_manquant_ou_nul");

        // TODO - créer un nouveau jeu

        String gameId = UUID.randomUUID().toString();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(gameId + params.toString());
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<Object> getGame(@PathVariable String gameId) {
        // TODO - récupérer le jeu en question
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gameId);
    }

    @GetMapping("/{gameId}/tokens/from-board")
    public ResponseEntity<Map<CellPosition, Token>> getBoardTokens(@PathVariable UUID gameId) {
        // TODO - Récupérer les jetons du plateau
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(null);
    }

    @GetMapping("/{gameId}/tokens/from-remaining")
    public ResponseEntity<Collection<Token>> getRemainingTokens(@PathVariable UUID gameId) {
        // TODO - Récupérer les jetons de la pioche
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(null);
    }

    @GetMapping("/{gameId}/tokens/from-removed")
    public ResponseEntity<Collection<Token>> getRemovedTokens(@PathVariable UUID gameId) {
        // TODO - Récupérer les jetons de la défausse
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(null);
    }

    @GetMapping("/{gameId}/allowed-moves/from-remaining")
    public ResponseEntity<Set<CellPosition>> getAllowedMovesFromRemaining(@PathVariable UUID gameId, @RequestParam String name) {
        // TODO - récupérer les moves possible pour un pion de la pioche
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new HashSet<>());
    }

    @GetMapping("/{gameId}/allowed-moves/from-board")
    public ResponseEntity<Set<CellPosition>> getAllowedMovesFromBoard(@PathVariable UUID gameId, @RequestParam int x, @RequestParam int y) {
        // TODO - récupérer les moves possibles pour un pion du plateau
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new HashSet<>());
    }

    @PostMapping("/{gameId}/play-move")
    public ResponseEntity<String> playMove(@PathVariable UUID gameId, @RequestBody GameMoveParam gameMove) {

        if (gameMove.fromBoard() == null || gameMove.toCell() == null)
            return ResponseEntity
                    .status((HttpStatus.BAD_REQUEST))
                    .body("Champ_manquant_ou_nul");

        // TODO - éxécuter le Move
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Joli_coup_!" + gameMove.toString());
    }
}