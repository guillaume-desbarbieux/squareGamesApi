package com.guillaume.squareGamesApi.controller;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.guillaume.squareGamesApi.model.SensorModel;
import com.guillaume.squareGamesApi.service.HeartBeatService;
import com.guillaume.squareGamesApi.service.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;


@RestController
public class HeartbeatController {

    @Autowired
    private final HeartBeatService heartBeatService;

    @Autowired
    private final SensorService sensorService;

    public HeartbeatController(HeartBeatService heartBeatService, SensorService sensorService) {
        this.heartBeatService = heartBeatService;
        this.sensorService = sensorService;
    }

    @GetMapping("heartbeat")
    public int getHeartbeat() {
        return heartBeatService.get();
    }


    @GetMapping("sensors")
    public MappingJacksonValue getSensors() {
        List<SensorModel> sensors = sensorService.findAll();
        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("secret");
        FilterProvider listeDeNosFiltres = new SimpleFilterProvider().addFilter("monFiltreDynamique", monFiltre);
        MappingJacksonValue produitsFiltres = new MappingJacksonValue(sensors);
        produitsFiltres.setFilters(listeDeNosFiltres);

        return produitsFiltres;
    }

    @GetMapping("sensors/{id}")
    public MappingJacksonValue getSensor(@PathVariable int id) {
        if (id < 0)
            return null;
        SensorModel sensor = sensorService.findById(id);
        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("secret");
        FilterProvider listeDeNosFiltres = new SimpleFilterProvider().addFilter("monFiltreDynamique", monFiltre);
        MappingJacksonValue produitFiltre = new MappingJacksonValue(sensor);
        produitFiltre.setFilters(listeDeNosFiltres);

        return produitFiltre;
    }

    @PutMapping("sensors/{id}")
    public SensorModel setSensor(@PathVariable int id, @RequestBody SensorModel sensor) {
        return sensorService.update(id, sensor);
    }

    @PostMapping("sensors")
    public ResponseEntity<SensorModel> saveSensor(@RequestBody SensorModel sensor) {
        SensorModel newSensor = sensorService.save(sensor);
        if (newSensor == null || newSensor.getName() == null)
            return ResponseEntity.noContent().build();
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newSensor.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("sensors/{id}")
    public String deleteSensor(@PathVariable int id) {
        return sensorService.delete(id);
    }
}
