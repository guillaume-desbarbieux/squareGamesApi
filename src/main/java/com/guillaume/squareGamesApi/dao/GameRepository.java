package com.guillaume.squareGamesApi.dao;

import com.guillaume.squareGamesApi.model.GameModel;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface GameRepository extends CrudRepository<GameModel, Integer> {
    @Query("SELECT g.uuid FROM GameModel g JOIN PlayerGameModel p ON p.gameUuid = g.uuid WHERE p.playerUuid = :playerUuid")
    List<UUID> findGameUuidsByPlayerUuid(String playerUuid);

    GameModel findByUuid(String uuid);

    boolean existsByUuid(String gameId);

    @Modifying
    @Transactional
    void deleteByUuid(String gameId);
}