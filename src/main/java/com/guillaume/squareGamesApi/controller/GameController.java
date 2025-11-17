package com.guillaume.squareGamesApi.controller;

import com.guillaume.squareGamesApi.model.GameCreationParams;
import com.guillaume.squareGamesApi.model.GameMoveParam;
import com.guillaume.squareGamesApi.service.GameService;
import fr.le_campus_numerique.square_games.engine.CellPosition;
import fr.le_campus_numerique.square_games.engine.Game;
import fr.le_campus_numerique.square_games.engine.InvalidPositionException;
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

    @GetMapping("/existing")
    public ResponseEntity<Collection<Game>> getGames() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gameService.getGames());
    }

    @PostMapping
    public ResponseEntity<String> createGame(@RequestBody GameCreationParams params) {
        if (params.playerCount() == 0 || params.boardSize() == 0 ||
                (!Objects.equals(params.identifier(), "tictactoe") && !Objects.equals(params.identifier(), "connect4") && !Objects.equals(params.identifier(), "15 puzzle")))
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Champ_manquant_ou_invalide");

        UUID gameId = gameService.createGame(params);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(gameId.toString());
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<Object> getGame(@PathVariable UUID gameId) {

        Game game = gameService.getGame(gameId);

        if (game == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(game);
    }

    @GetMapping("/{gameId}/tokens/from-board")
    public ResponseEntity<Map<CellPosition, Token>> getBoardTokens(@PathVariable UUID gameId) {

        if (gameService.getGame(gameId) == null)
            return ResponseEntity.notFound().build();

        Map<CellPosition, Token> tokens = gameService.getBoardTokens(gameId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tokens);
    }

    @GetMapping("/{gameId}/tokens/from-remaining")
    public ResponseEntity<Collection<Token>> getRemainingTokens(@PathVariable UUID gameId) {

        if (gameService.getGame(gameId) == null)
            return ResponseEntity.notFound().build();

        Collection<Token> tokens = gameService.getRemainingTokens(gameId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tokens);
    }

    @GetMapping("/{gameId}/tokens/from-removed")
    public ResponseEntity<Collection<Token>> getRemovedTokens(@PathVariable UUID gameId) {

        if (gameService.getGame(gameId) == null)
            return ResponseEntity.notFound().build();

        Collection<Token> tokens = gameService.getRemovedTokens(gameId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tokens);
    }

    @GetMapping("/{gameId}/allowed-moves/from-remaining")
    public ResponseEntity<Set<CellPosition>> getAllowedMovesFromRemaining(@PathVariable UUID gameId, @RequestParam String name) {

        if (gameService.getGame(gameId) == null) {
            return ResponseEntity.notFound().build();
        }
        Set<CellPosition> moves = gameService.getAllowedMovesFromRemaining(gameId, name);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(moves);
    }

    @GetMapping("/{gameId}/allowed-moves/from-board")
    public ResponseEntity<Set<CellPosition>> getAllowedMovesFromBoard(@PathVariable UUID gameId, @RequestParam int x, @RequestParam int y) {

        CellPosition cellPosition = new CellPosition(x,y);

        if (gameService.getGame(gameId) == null)
            return ResponseEntity.notFound().build();

        Set<CellPosition> moves = gameService.getAllowedMovesFromBoard(gameId, cellPosition);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(moves);
    }

    @PostMapping("/{gameId}/play-move")
    public ResponseEntity<String> playMove(@PathVariable UUID gameId, @RequestBody GameMoveParam gameMove) {

        if (gameService.getGame(gameId) == null)
            return ResponseEntity.notFound().build();

        if (gameMove.toCell() == null)
            return ResponseEntity
                    .status((HttpStatus.BAD_REQUEST))
                    .body("Champ_manquant_ou_nul");

        try {
            gameService.playMove(gameId, gameMove);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Joli_coup_!" + gameMove);
        } catch (InvalidPositionException | IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        }
    }
}