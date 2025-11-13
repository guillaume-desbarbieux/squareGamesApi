package com.guillaume.squareGamesApi.service;

import com.guillaume.squareGamesApi.model.SensorModel;

import java.util.List;

public interface SensorService {
    List<SensorModel> findAll();
    SensorModel findById(int id);
    SensorModel save(SensorModel sensor);
    SensorModel update(int id, SensorModel sensor);
    String delete(int id);

}
