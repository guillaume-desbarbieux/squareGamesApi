package com.guillaume.squareGamesApi.controller;

import com.guillaume.squareGamesApi.model.dao.HeartbeatSensor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RestController
public class HeartbeatController {

    @Autowired
    private HeartbeatSensor heartbeatSensor;

    @GetMapping("heartbeat")
    public int getHeartBeat() {
        return heartbeatSensor.get();
    }

    @GetMapping("heartbeat/{quantity}")
    public List<Integer> getHeartBeatList(@PathVariable int quantity){
        List<Integer> list = new ArrayList<>();
        for (int i = 0 ; i < quantity ; i++)
            list.add(heartbeatSensor.get());
        return list;
    }
}
