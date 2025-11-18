package com.guillaume.squareGamesApi.service;

import com.guillaume.squareGamesApi.dao.SensorDao;
import com.guillaume.squareGamesApi.model.SensorModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SensorServiceImpl implements SensorService {

    private final SensorDao sensorDao;

    public SensorServiceImpl(SensorDao sensorDao) {
        this.sensorDao = sensorDao;
    }

    @Override
    public List<SensorModel> findAll() {
        return sensorDao.findAll();
    }

    @Override
    public SensorModel findById(int id) {
        return sensorDao.findById(id);
    }

    @Override
    public SensorModel save(SensorModel sensor) {
        return sensorDao.save(sensor);
    }

    @Override
    public SensorModel update(int id, SensorModel sensor) {
        if (sensor.getName() == null || sensor.getName().isEmpty())
            return null;
        return sensorDao.update(id, sensor);
    }

    @Override
    public String delete(int id) {
        if (sensorDao.findById(id) != null) {
            sensorDao.delete(id);
            return "{'message' : 'Sensor have been deleted'}";
        } else
            return "{'message' : 'Sensor can't be found'}";
    }
}
