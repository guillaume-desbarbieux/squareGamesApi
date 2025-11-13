package com.guillaume.squareGamesApi.model.dao;

import org.springframework.stereotype.Service;

@Service
public class RandomHeartBeat implements HeartbeatSensor {
    @Override
    public int get() {
        return 40 + (int) Math.floor(Math.random() * 191);
    }
}
