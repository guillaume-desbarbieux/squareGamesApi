package com.guillaume.squareGamesApi.controller;

import com.guillaume.squareGamesApi.model.GameCreationParams;
import com.guillaume.squareGamesApi.model.GameMoveParam;
import com.guillaume.squareGamesApi.service.GameService;
import fr.le_campus_numerique.square_games.engine.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/games")
public class GameController {

    @Autowired
    private GameService gameService;

    @GetMapping()
    public ResponseEntity<Collection<String>> getGameIdentifiers() {
        Collection<String> gameIdentifiers = gameService.getGameIdentifiers();

        if (gameIdentifiers.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body(gameIdentifiers);
        else
            return ResponseEntity.ok(gameIdentifiers);
    }

    @GetMapping("/UUID")
    public ResponseEntity<Collection<Game>> getGamesUUID() {
        Collection<Game> gameUUIDs = gameService.getGames();

        if (gameUUIDs.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body(gameUUIDs);
        else
            return ResponseEntity.ok(gameUUIDs);
    }

    @PostMapping
    public ResponseEntity<String> createGame(@RequestBody GameCreationParams params) {
        if (!gameService.getGameIdentifiers().contains(params.identifier())
                || params.boardSize() == 0)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Champ_manquant_ou_invalide");

        try {
            UUID gameId = gameService.createGame(params);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(gameId)
                    .toUri();
            return ResponseEntity.created(location).body(gameId.toString());

        } catch (IllegalArgumentException | InconsistentGameDefinitionException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<Object> getGame(@PathVariable UUID gameId) {

        Game game = gameService.getGame(gameId);

        if (game == null)
            return ResponseEntity.notFound().build();
        else
            return ResponseEntity.ok(game);
    }

    @GetMapping("/{gameId}/tokens/from-board")
    public ResponseEntity<Map<CellPosition, Token>> getBoardTokens(@PathVariable UUID gameId) {

        if (gameService.getGame(gameId) == null)
            return ResponseEntity.notFound().build();

        Map<CellPosition, Token> tokens = gameService.getBoardTokens(gameId);
        if (tokens.isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(tokens);
        else
            return ResponseEntity.ok(gameService.getBoardTokens(gameId));
    }

    @GetMapping("/{gameId}/tokens/from-remaining")
    public ResponseEntity<Collection<Token>> getRemainingTokens(@PathVariable UUID gameId) {

        if (gameService.getGame(gameId) == null)
            return ResponseEntity.notFound().build();

        Collection<Token> tokens = gameService.getRemainingTokens(gameId);
        if (tokens.isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        else
            return ResponseEntity.ok(gameService.getRemainingTokens(gameId));
    }

    @GetMapping("/{gameId}/tokens/from-removed")
    public ResponseEntity<Collection<Token>> getRemovedTokens(@PathVariable UUID gameId) {

        if (gameService.getGame(gameId) == null)
            return ResponseEntity.notFound().build();

        Collection<Token> tokens = gameService.getRemovedTokens(gameId);
        if (tokens.isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        else
            return ResponseEntity.ok(tokens);
    }

    @GetMapping("/{gameId}/allowed-moves/from-remaining")
    public ResponseEntity<Set<CellPosition>> getAllowedMovesFromRemaining(@PathVariable UUID gameId, @RequestParam String name) {

        if (gameService.getGame(gameId) == null)
            return ResponseEntity.notFound().build();

        Set<CellPosition> moves = gameService.getAllowedMovesFromRemaining(gameId, name);
        if (moves.isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        else
            return ResponseEntity.ok(moves);
    }

    @GetMapping("/{gameId}/allowed-moves/from-board")
    public ResponseEntity<Set<CellPosition>> getAllowedMovesFromBoard(@PathVariable UUID gameId, @RequestParam int x, @RequestParam int y) {

        if (gameService.getGame(gameId) == null)
            return ResponseEntity.notFound().build();

        Set<CellPosition> moves = gameService.getAllowedMovesFromBoard(gameId, new CellPosition(x, y));
        if (moves.isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        else
            return ResponseEntity.ok(moves);
    }

    @PostMapping("/{gameId}/play-move")
    public ResponseEntity<String> playMove(@PathVariable UUID gameId, @RequestBody GameMoveParam gameMove) {

        if (gameService.getGame(gameId) == null)
            return ResponseEntity.notFound().build();

        if (gameMove.toCell() == null
                || (gameMove.name() == null && gameMove.fromCell() == null))
            return ResponseEntity
                    .status((HttpStatus.BAD_REQUEST))
                    .body("Champ_manquant_ou_nul");

        try {
            gameService.playMove(gameId, gameMove);
            return ResponseEntity.ok("Joli_coup_!" + gameMove);
        } catch (InvalidPositionException | IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        }
    }
}