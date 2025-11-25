package com.guillaume.squareGamesApi.dao;

import com.guillaume.squareGamesApi.model.GameModel;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface GameRepository extends CrudRepository<GameModel, Integer> {
    @Query("SELECT g.uuid from GameModel g")
    List<UUID> findAllUuids();

    GameModel findByUuid(UUID uuid);

    boolean existsByUuid(UUID gameId);

    @Modifying
    @Transactional
    void deleteByUuid(UUID gameId);
}