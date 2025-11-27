package com.guillaume.squareGamesApi.dao;

import com.guillaume.squareGamesApi.model.BoardTokenModel;
import com.guillaume.squareGamesApi.model.GameModel;
import com.guillaume.squareGamesApi.model.PlayerGameModel;
import com.guillaume.squareGamesApi.model.RemovedTokenModel;
import com.guillaume.squareGamesApi.service.GamePlugin;
import fr.le_campus_numerique.square_games.engine.Game;
import fr.le_campus_numerique.square_games.engine.InconsistentGameDefinitionException;
import fr.le_campus_numerique.square_games.engine.Token;
import fr.le_campus_numerique.square_games.engine.TokenPosition;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Repository
@Primary
public class JPAGameDAO implements GameDAO {

    private final Map<String, GamePlugin> pluginMap;

    private final GameRepository gameRepository;
    private final BoardTokenRepository boardTokenRepository;
    private final RemovedTokenRepository removedTokenRepository;
    private final PlayerGameRepository playerGameRepository;

    public JPAGameDAO(List<GamePlugin> plugins, GameRepository gameRepository, BoardTokenRepository boardTokenRepository, RemovedTokenRepository removedTokenRepository, PlayerGameRepository playerGameRepository) {
        this.gameRepository = gameRepository;
        this.boardTokenRepository = boardTokenRepository;
        this.removedTokenRepository = removedTokenRepository;
        this.playerGameRepository = playerGameRepository;

        pluginMap = new HashMap<>();
        for (GamePlugin plugin : plugins)
            pluginMap.put(plugin.getGameIdentifier(), plugin);
    }


    @Override
    public boolean existId(UUID gameId) {
        return gameRepository.existsByUuid(gameId.toString());
    }

    @Override
    public Game getGameById(UUID gameId) throws SquareGamesDAOException {
        GameModel gameModel = gameRepository.findByUuid(gameId.toString());
        if (gameModel == null)
            return null;

        List<UUID> players = playerGameRepository.findPlayerUuidByGameUuid(gameId.toString());

        Collection<TokenPosition<UUID>> boardTokens = new ArrayList<>();
        for (BoardTokenModel boardTokenModel : boardTokenRepository.findByGameUUID(gameId.toString()))
            boardTokens.add(new TokenPosition<>(
                    UUID.fromString(boardTokenModel.getPlayerUUID()),
                    boardTokenModel.getName(),
                    boardTokenModel.getX(),
                    boardTokenModel.getY()));


        Collection<TokenPosition<UUID>> removedTokens = new ArrayList<>();
        for (RemovedTokenModel removedTokenModel : removedTokenRepository.findByGameUUID(gameId.toString()))
                removedTokens.add(new TokenPosition<>(
                        UUID.fromString(removedTokenModel.getPlayerUUID()),
                        removedTokenModel.getName(),
                        removedTokenModel.getX(),
                        removedTokenModel.getY()));

        GamePlugin plugin = pluginMap.get(gameModel.getGameType());

        try {
            return plugin.createGameWithIds(
                    UUID.fromString(gameModel.getUuid()),
                    gameModel.getBoardSize(),
                    players,
                    boardTokens,
                    removedTokens);
        } catch (
                InconsistentGameDefinitionException e) {
            System.out.println("Error when creating : " + e.getMessage());
            return null;
        }

    }

    @Override
    public UUID addGame(Game game) throws SquareGamesDAOException {

        GameModel gameModel = new GameModel();
        gameModel.setGameType(game.getFactoryId());
        gameModel.setBoardSize(game.getBoardSize());
        gameModel.setUuid(game.getId().toString());
        gameRepository.save(gameModel);

        for (UUID player : game.getPlayerIds()) {
            PlayerGameModel playerGameModel = new PlayerGameModel();
            playerGameModel.setPlayerUuid(player.toString());
            playerGameModel.setGameUuid(game.getId().toString());
            playerGameRepository.save(playerGameModel);
        }

        for (Token token : game.getBoard().values()) {
            BoardTokenModel boardTokenModel = new BoardTokenModel();
            boardTokenModel.setGameUUID(game.getId().toString());
            boardTokenModel.setPlayerUUID(token.getOwnerId().orElseThrow().toString());
            boardTokenModel.setName(token.getName());
            boardTokenModel.setX(token.getPosition().x());
            boardTokenModel.setY(token.getPosition().y());
            boardTokenRepository.save(boardTokenModel);
        }

        for (Token token : game.getRemovedTokens()) {
            RemovedTokenModel removedTokenModel = new RemovedTokenModel();
            removedTokenModel.setGameUUID(game.getId().toString());
            removedTokenModel.setPlayerUUID(token.getOwnerId().orElseThrow().toString());
            removedTokenModel.setName(token.getName());
            removedTokenModel.setX(token.getPosition().x());
            removedTokenModel.setY(token.getPosition().y());
            removedTokenRepository.save(removedTokenModel);
        }
        return game.getId();
    }

    @Transactional
    @Override
    public boolean updateGame(Game game) throws SquareGamesDAOException {
        deleteGame(game.getId());
        return addGame(game) != null;
    }

    @Transactional
    @Override
    public boolean deleteGame(UUID gameId) throws SquareGamesDAOException {
        gameRepository.deleteByUuid(gameId.toString());
        playerGameRepository.deleteAllByGameUuid(gameId.toString());
        boardTokenRepository.deleteAllByGameUUID(gameId.toString());
        removedTokenRepository.deleteAllByGameUUID(gameId.toString());
        return true;
    }

    @Override
    public Collection<UUID> getGameUUIDs(UUID userId) {
        return gameRepository.findGameUuidsByPlayerUuid(userId.toString());
    }
}
