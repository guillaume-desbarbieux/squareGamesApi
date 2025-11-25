package com.guillaume.squareGamesApi.dao;

import com.guillaume.squareGamesApi.model.RemovedToken;
import org.springframework.data.repository.CrudRepository;

public interface RemovedTokenRepository extends CrudRepository<RemovedToken, Integer> {
}
