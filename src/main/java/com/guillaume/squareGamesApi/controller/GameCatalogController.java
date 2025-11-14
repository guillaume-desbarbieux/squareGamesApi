package com.guillaume.squareGamesApi.controller;

import com.guillaume.squareGamesApi.service.GameCatalog;
import org.springframework.web.bind.annotation.*;
import java.util.Collection;


@RestController
@RequestMapping("games")
public class GameCatalogController {

    GameCatalog gameCatalog;
    public GameCatalogController(GameCatalog gameCatalog) {
        this.gameCatalog = gameCatalog;
    }

    @GetMapping()
    public Collection<String> getGameIdentifiers() {
        return gameCatalog.getGameIdentifiers();
    }
/*
    //Body : Json {identifier, playerCount, boardSize)
    @PostMapping()
    public ResponseEntity<UUID> createGame(@RequestBody Game game) {
        return gameCatalog.create(game);
    }

    @GetMapping("/{gameId}/status")
    public ResponseEntity<GameStatus> getGameStatus(@PathVariable UUID gameId) {
        return gameCatalog.getStatus(gameId);
    }

    @GetMapping("/{gameId}/players")
    public ResponseEntity<Set<UUID>> getPlayerIds(@PathVariable UUID gameId) {
        return gameCatalog.getPlayers(gameId);
    }

    @GetMapping("/{gameId}/players/current")
    public ResponseEntity<UUID> getCurrentPlayerId(@PathVariable UUID gameId) {
        return gameCatalog.getCurrentPlayer(gameId);
    }

    @GetMapping("/{gameId}/board-size")
    public ResponseEntity<Integer> getBoardSize(@PathVariable UUID gameId) {
        return gameCatalog.getBoardSize(gameId);
    }

    @GetMapping("/{gameId}/tokens/board")
    public ResponseEntity<Map<CellPosition, Token>> getBoardTokens(@PathVariable UUID gameId) {
        return gameCatalog.getBoard(gameId);
    }

    @GetMapping("/{gameId}/tokens/remaining")
    public ResponseEntity<Collection<Token>> getRemainingTokens(@PathVariable UUID gameId) {
        return gameCatalog.getRemainingTokens(gameId);
    }

    @GetMapping("/{gameId}/tokens/removed")
    public ResponseEntity<Collection<Token>> getRemovedTokens(@PathVariable UUID gameId) {
        return gameCatalog.getRemovedTokens(gameId);
    }

    @GetMapping("/{gameId}/moves/remaining")
    public ResponseEntity<Set<CellPosition>> getAllowedMovesFromRemaining(@PathVariable UUID gameId, @RequestParam String name) {
        return gameCatalog.getAllowedMovesFromRemaining(gameId, name);
    }

    @GetMapping("/{gameId}/moves/board")
    public ResponseEntity<Set<CellPosition>> getAllowedMovesFromBoard(@PathVariable UUID gameId, @RequestParam int x, @RequestParam int y) {
        return gameCatalog.getAllowedMovesFromBoard(gameId, x, y);
    }

    //GameMove {source = board || remaining, name, fromX, fromY, @Required toX, @Required toY}
    @PostMapping("/{gameId}/play")
    public ResponseEntity<String> playMove(@PathVariable UUID gameId, @RequestBody GameMove gameMove) {
        return gameCatalog.playMove(gameId, gameMove);
    }

 */
}
