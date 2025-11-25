package com.guillaume.squareGamesApi.dao;

import com.guillaume.squareGamesApi.model.BoardToken;
import org.springframework.data.repository.CrudRepository;

public interface BoardTokenRepository extends CrudRepository<BoardToken, Integer> {
}
