package com.guillaume.squareGamesApi.dao;

import com.guillaume.squareGamesApi.model.SensorModel;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class SensorDaoImpl implements SensorDao {

    public static List<SensorModel> sensors = new ArrayList<>();

    static {
        sensors.add(new SensorModel(1, "Montre", "Si tu lis ça, il faut revoir la sécu !", "détails longs, fastidieux et inutiles sur ce capteur"));
        sensors.add(new SensorModel(2, "Ceinture", "Si tu lis ça, il faut revoir la sécu !", "détails longs, fastidieux et inutiles sur ce capteur"));
        sensors.add(new SensorModel(3, "Cardio","Si tu lis ça, il faut revoir la sécu !", "détails longs, fastidieux et inutiles sur ce capteur"));
    }

    @Override
    public List<SensorModel> findAll() {
        return sensors;
    }

    @Override
    public SensorModel findById(int id) {
        for (SensorModel sensor : sensors)
            if (sensor.getId() == id)
                return sensor;
        return null;
    }

    @Override
    public SensorModel update(int id, SensorModel sensor) {
        SensorModel oldSensor = findById(id);
        if (oldSensor != null)
            oldSensor.setName(sensor.getName());
        return oldSensor;
    }

    @Override
    public SensorModel save(SensorModel sensor) {
        int maxId = 0;
        for (SensorModel s : sensors)
            if (s.getId() > maxId)
                maxId = s.getId();
        sensor.setId(maxId + 1);
        sensors.add(sensor);
        return sensor;
    }

    @Override
    public String delete(int id) {
        SensorModel sensor = findById(id);
        sensors.remove(sensor);
        return "sensor has been removed";
    }
}
