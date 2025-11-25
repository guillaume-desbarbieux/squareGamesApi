package com.guillaume.squareGamesApi.dao;

import com.guillaume.squareGamesApi.model.Player;
import org.springframework.data.repository.CrudRepository;

public interface PlayerRepository extends CrudRepository<Player, Integer> {
}
