package com.guillaume.squareGamesApi.controller;

import com.guillaume.squareGamesApi.model.GameCreationParams;
import com.guillaume.squareGamesApi.model.GameMoveParam;
import com.guillaume.squareGamesApi.service.GameService;
import com.guillaume.squareGamesApi.service.SquareGameUnauthorizedException;
import fr.le_campus_numerique.square_games.engine.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.context.i18n.LocaleContextHolder;
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

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping()
    public ResponseEntity<Map<String, String>> getCatalog() {
        Locale locale = LocaleContextHolder.getLocale();
        Map<String, String> catalog = gameService.getCatalog(locale);

        if (catalog.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .build();
        else
            return ResponseEntity.ok(catalog);
    }

    @GetMapping("/UUID")
    public ResponseEntity<Collection<UUID>> getGameUUIDs(@RequestHeader("X-UserId") UUID userId) {
        Collection<UUID> gameUUIDs = gameService.getGameUUIDs(userId);

        if (gameUUIDs.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .build();

        return ResponseEntity.ok(gameUUIDs);
    }

    @Operation(
            summary = "Create a game",
            description = "Create a game choosing beetwen Taquin, TicTacToe and Connect Four",
            tags = {"Game", "Creation"})
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(schema = @Schema(implementation = Game.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())})

    })
    @PostMapping
    public ResponseEntity<String> createGame(@RequestBody GameCreationParams params, @RequestHeader("X-UserId") UUID userId) {
        if (!gameService.getGameIdentifiers().contains(params.identifier()))
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("unknown_Game_identifier");

        try {
            UUID gameId = gameService.createGame(params, userId);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(gameId)
                    .toUri();
            return ResponseEntity.created(location).body(gameId.toString());

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<GameDTO> getGame(@PathVariable UUID gameId, @RequestHeader("X-UserId") UUID userId) {
        try {
            Game game = gameService.getGame(gameId, userId);

            if (game == null)
                return ResponseEntity.notFound().build();
            else
                return ResponseEntity.ok(toDTO(game));
        } catch (SquareGameUnauthorizedException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private GameDTO toDTO(Game game) {
        return new GameDTO(
                game.getId(),
                game.getBoardSize(),
                game.getStatus(),
                toDTO(game.getBoard()),
                toDTO(game.getRemainingTokens()),
                toDTO(game.getRemovedTokens()),
                game.getPlayerIds()
        );
    }

    private Map<CellPosition, TokenDTO> toDTO(Map<CellPosition, Token> board) {
        Map<CellPosition, TokenDTO> boardDTO = new HashMap<>();
        for (Token token : board.values())
            boardDTO.put(token.getPosition(), toDTO(token));
        return boardDTO;
    }

    private TokenDTO toDTO(Token token) {
        return new TokenDTO(
                token.getOwnerId(),
                token.getName(),
                token.getPosition()
        );
    }

    private Collection<TokenDTO> toDTO(Collection<Token> tokens) {
        Collection<TokenDTO> dto = new ArrayList<>();
        for (Token token : tokens)
            dto.add(toDTO(token));
        return dto;
    }


    @GetMapping("/{gameId}/tokens/from-board")
    public ResponseEntity<Map<CellPosition, TokenDTO>> getBoardTokens(@PathVariable UUID gameId, @RequestHeader("X-UserId") UUID userId) {
        try {
            if (gameService.getGame(gameId, userId) == null) {
                return ResponseEntity.notFound().build();
            }
            Map<CellPosition, Token> tokens = gameService.getBoardTokens(gameId, userId);
            if (tokens.isEmpty())
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            else
                return ResponseEntity.ok(toDTO(tokens));

        } catch (
                SquareGameUnauthorizedException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/{gameId}/tokens/from-remaining")
    public ResponseEntity<Collection<TokenDTO>> getRemainingTokens(@PathVariable UUID gameId, @RequestHeader("X-UserId") UUID userId) {
        try {
            if (gameService.getGame(gameId, userId) == null)
                return ResponseEntity.notFound().build();

            Collection<Token> tokens = gameService.getRemainingTokens(gameId, userId);
            if (tokens.isEmpty())
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            else
                return ResponseEntity.ok(toDTO(tokens));
        } catch (SquareGameUnauthorizedException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/{gameId}/tokens/from-removed")
    public ResponseEntity<Collection<TokenDTO>> getRemovedTokens(@PathVariable UUID gameId, @RequestHeader("X-UserId") UUID userId) {
        try {
            if (gameService.getGame(gameId, userId) == null)
                return ResponseEntity.notFound().build();

            Collection<Token> tokens = gameService.getRemovedTokens(gameId, userId);
            if (tokens.isEmpty())
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            else
                return ResponseEntity.ok(toDTO(tokens));
        } catch (SquareGameUnauthorizedException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/{gameId}/allowed-moves/from-remaining")
    public ResponseEntity<Set<CellPosition>> getAllowedMovesFromRemaining(@PathVariable UUID gameId, @RequestParam String name, @RequestHeader("X-UserId") UUID userId) {
        try {
            if (gameService.getGame(gameId, userId) == null)
                return ResponseEntity.notFound().build();

            Set<CellPosition> moves = gameService.getAllowedMovesFromRemaining(gameId, name, userId);
            if (moves.isEmpty())
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            else
                return ResponseEntity.ok(moves);
        } catch (SquareGameUnauthorizedException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/{gameId}/allowed-moves/from-board")
    public ResponseEntity<Set<CellPosition>> getAllowedMovesFromBoard(@PathVariable UUID gameId, @RequestParam int x, @RequestParam int y, @RequestHeader("X-UserId") UUID userId) {
        try {
            if (gameService.getGame(gameId, userId) == null)
                return ResponseEntity.notFound().build();

            Set<CellPosition> moves = gameService.getAllowedMovesFromBoard(gameId, new CellPosition(x, y), userId);
            if (moves.isEmpty())
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            else
                return ResponseEntity.ok(moves);
        } catch (SquareGameUnauthorizedException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/{gameId}/play-move")
    public ResponseEntity<String> playMove(@PathVariable UUID gameId, @RequestBody GameMoveParam gameMove, @RequestHeader("X-UserId") UUID userId) {
        try {
            if (gameService.getGame(gameId, userId) == null)
                return ResponseEntity.notFound().build();

            if (gameMove.toCell() == null
                    || (gameMove.name() == null && gameMove.fromCell() == null))
                return ResponseEntity
                        .status((HttpStatus.BAD_REQUEST))
                        .body("Champ_manquant_ou_nul");

            try {
                gameService.playMove(gameId, gameMove, userId);
                return ResponseEntity.ok("Joli_coup_!" + gameMove);
            } catch (InvalidPositionException | IllegalArgumentException e) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(e.getMessage());
            }
        } catch (SquareGameUnauthorizedException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @DeleteMapping("/{gameId}")
    public ResponseEntity<String> deleteGame(@PathVariable UUID gameId, @RequestHeader("X-UserId") UUID userId) {
        try {
            Game game = gameService.getGame(gameId, userId);


            if (game == null)
                return ResponseEntity.notFound().build();

            Boolean deleted = gameService.deleteGame(gameId, userId);
            if (deleted)
                return ResponseEntity.ok("Successfully deleted.");
            else
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        } catch (
                SquareGameUnauthorizedException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
