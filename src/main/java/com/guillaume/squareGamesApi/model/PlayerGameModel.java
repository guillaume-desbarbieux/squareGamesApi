package com.guillaume.squareGamesApi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class PlayerGameModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private UUID playerUuid;
    private UUID gameUuid;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public void setPlayerUuid(UUID playerUuid) {
        this.playerUuid = playerUuid;
    }

    public UUID getGameUuid() {
        return gameUuid;
    }

    public void setGameUuid(UUID gameUuid) {
        this.gameUuid = gameUuid;
    }
}
