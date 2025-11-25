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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Repository
@Primary
public class JPAGameDAO implements GameDAO {

    private final Map<String, GamePlugin> pluginMap;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private BoardTokenRepository boardTokenRepository;

    @Autowired
    private RemovedTokenRepository removedTokenRepository;

    @Autowired
    private PlayerGameRepository playerGameRepository;

    public JPAGameDAO(List<GamePlugin> plugins) {
        pluginMap = new HashMap<>();
        for (GamePlugin plugin : plugins)
            pluginMap.put(plugin.getGameIdentifier(), plugin);
    }


    @Override
    public boolean existId(UUID gameId) {
        return gameRepository.existsByUuid(gameId);
    }

    @Override
    public Collection<Game> getGames() {
        Collection<Game> games = new ArrayList<>();
        for (UUID gameUuid : getGameUUIDs())
            games.add(getGameById(gameUuid));
        return games;
    }

    @Override
    public Game getGameById(UUID gameId) throws SquareGamesDAOException {
        GameModel gameModel = gameRepository.findByUuid(gameId);
        if (gameModel == null)
            return null;

        List<UUID> players = playerGameRepository.findPlayerUuidByGameUuid(gameId);

        Collection<TokenPosition<UUID>> boardTokens = new ArrayList<>();
        for (BoardTokenModel boardTokenModel : boardTokenRepository.findByGameUUID(gameId))
            boardTokens.add(new TokenPosition<>(
                    boardTokenModel.getPlayerUUID(),
                    boardTokenModel.getName(),
                    boardTokenModel.getX(),
                    boardTokenModel.getY()));


        Collection<TokenPosition<UUID>> removedTokens = new ArrayList<>();
        for (RemovedTokenModel removedTokenModel : removedTokenRepository.findByGameUUID(gameId))
                removedTokens.add(new TokenPosition<>(
                        removedTokenModel.getPlayerUUID(),
                        removedTokenModel.getName(),
                        removedTokenModel.getX(),
                        removedTokenModel.getY()));

        GamePlugin plugin = pluginMap.get(gameModel.getGameType());

        try {
            return plugin.createGameWithIds(
                    gameModel.getUuid(),
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
        gameModel.setUuid(game.getId());
        gameRepository.save(gameModel);

        for (UUID player : game.getPlayerIds()) {
            PlayerGameModel playerGameModel = new PlayerGameModel();
            playerGameModel.setPlayerUuid(player);
            playerGameModel.setGameUuid(game.getId());
            playerGameRepository.save(playerGameModel);
        }

        for (Token token : game.getBoard().values()) {
            BoardTokenModel boardTokenModel = new BoardTokenModel();
            boardTokenModel.setGameUUID(game.getId());
            boardTokenModel.setPlayerUUID(token.getOwnerId().orElseThrow());
            boardTokenModel.setName(token.getName());
            boardTokenModel.setX(token.getPosition().x());
            boardTokenModel.setY(token.getPosition().y());
            boardTokenRepository.save(boardTokenModel);
        }

        for (Token token : game.getRemovedTokens()) {
            RemovedTokenModel removedTokenModel = new RemovedTokenModel();
            removedTokenModel.setGameUUID(game.getId());
            removedTokenModel.setPlayerUUID(token.getOwnerId().orElseThrow());
            removedTokenModel.setName(token.getName());
            removedTokenModel.setX(token.getPosition().x());
            removedTokenModel.setY(token.getPosition().y());
            removedTokenRepository.save(removedTokenModel);
        }
        return game.getId();
    }

    @Override
    public boolean updateGame(Game game) throws SquareGamesDAOException {
        deleteGame(game.getId());
        return addGame(game) != null;
    }

    @Transactional
    @Override
    public boolean deleteGame(UUID gameId) throws SquareGamesDAOException {
        gameRepository.deleteByUuid(gameId);
        playerGameRepository.deleteAllByGameUuid(gameId);
        boardTokenRepository.deleteAllByGameUUID(gameId);
        removedTokenRepository.deleteAllByGameUUID(gameId);
        return true;
    }

    @Override
    public Collection<UUID> getGameUUIDs() {
        return gameRepository.findAllUuids();
    }
}
