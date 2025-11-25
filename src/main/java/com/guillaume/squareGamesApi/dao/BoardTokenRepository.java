package com.guillaume.squareGamesApi.dao;

import com.guillaume.squareGamesApi.model.BoardTokenModel;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface BoardTokenRepository extends CrudRepository<BoardTokenModel, Integer> {

    List<BoardTokenModel> findByGameUUID(UUID gameUUID);

    @Modifying
    @Transactional
    void deleteAllByGameUUID(UUID gameUUID);
}
