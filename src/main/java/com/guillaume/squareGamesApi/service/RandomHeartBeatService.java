package com.guillaume.squareGamesApi.service;

import org.springframework.stereotype.Service;

@Service
public class RandomHeartBeatService implements HeartBeatService {

    @Override
    public int get() {
        int min = 40;
        int max = 230;
        return min + (int) Math.floor(Math.random() * (max - min + 1));
    }
}
