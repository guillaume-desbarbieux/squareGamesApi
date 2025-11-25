package com.guillaume.squareGamesApi.dao;

import com.guillaume.squareGamesApi.model.PlayerGameModel;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface PlayerGameRepository extends CrudRepository<PlayerGameModel, Integer> {

    @Query("SELECT p.playerUuid from PlayerGameModel p where p.gameUuid = :gameUuid")
    List<UUID> findPlayerUuidByGameUuid(String gameUuid);

    @Modifying
    @Transactional
    void deleteAllByGameUuid(String gameUuid);

}
