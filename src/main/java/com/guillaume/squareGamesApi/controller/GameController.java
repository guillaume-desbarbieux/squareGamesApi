package com.guillaume.squareGamesApi.controller;

import com.guillaume.squareGamesApi.model.GameCreationParams;
import com.guillaume.squareGamesApi.model.GameMoveParam;
import com.guillaume.squareGamesApi.service.GameService;
import fr.le_campus_numerique.square_games.engine.CellPosition;
import fr.le_campus_numerique.square_games.engine.Game;
import fr.le_campus_numerique.square_games.engine.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/games")
public class GameController {

    @Autowired
    private GameService gameService;

    @PostMapping
    public ResponseEntity<String> createGame(@RequestBody GameCreationParams params) {
        if (params.identifier() == null || params.playerCount() == 0 || params.boardSize() == 0)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Champ_manquant_ou_nul");

        UUID gameId = gameService.createGame(params);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(gameId.toString());
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<Object> getGame(@PathVariable UUID gameId) {

        Game game = gameService.getGame(gameId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gameId);
    }

    @GetMapping("/{gameId}/tokens/from-board")
    public ResponseEntity<Map<CellPosition, Token>> getBoardTokens(@PathVariable UUID gameId) {

        Map<CellPosition, Token> tokens = gameService.getBoardTokens(gameId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tokens);
    }

    @GetMapping("/{gameId}/tokens/from-remaining")
    public ResponseEntity<Collection<Token>> getRemainingTokens(@PathVariable UUID gameId) {

        Collection<Token> tokens = gameService.getRemainingTokens(gameId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tokens);
    }

    @GetMapping("/{gameId}/tokens/from-removed")
    public ResponseEntity<Collection<Token>> getRemovedTokens(@PathVariable UUID gameId) {

        Collection<Token> tokens = gameService.getRemovedTokens(gameId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tokens);
    }

    @GetMapping("/{gameId}/allowed-moves/from-remaining")
    public ResponseEntity<Set<CellPosition>> getAllowedMovesFromRemaining(@PathVariable UUID gameId, @RequestParam String name) {

        Set<CellPosition> moves = gameService.getAllowedMovesFromRemaining(gameId, name);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(moves);
    }

    @GetMapping("/{gameId}/allowed-moves/from-board")
    public ResponseEntity<Set<CellPosition>> getAllowedMovesFromBoard(@PathVariable UUID gameId, @RequestParam int x, @RequestParam int y) {

        CellPosition cellPosition = new CellPosition(x,y);
        Set<CellPosition> moves = gameService.getAllowedMovesFromBoard(gameId, cellPosition);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(moves);
    }

    @PostMapping("/{gameId}/play-move")
    public ResponseEntity<String> playMove(@PathVariable UUID gameId, @RequestBody GameMoveParam gameMove) {

        if (gameMove.fromBoard() == null || gameMove.toCell() == null)
            return ResponseEntity
                    .status((HttpStatus.BAD_REQUEST))
                    .body("Champ_manquant_ou_nul");

        Boolean played = gameService.playMove(gameId, gameMove);

        if (played)
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Joli_coup_!" + gameMove);
        else
            return ResponseEntity
                    .status(HttpStatus.I_AM_A_TEAPOT)
                    .body("Bad_Move");
    }
}