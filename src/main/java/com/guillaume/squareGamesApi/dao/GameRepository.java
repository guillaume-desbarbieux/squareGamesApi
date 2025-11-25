package com.guillaume.squareGamesApi.dao;

import com.guillaume.squareGamesApi.model.Game;
import org.springframework.data.repository.CrudRepository;

public interface GameRepository extends CrudRepository<Game, Integer> {
}
