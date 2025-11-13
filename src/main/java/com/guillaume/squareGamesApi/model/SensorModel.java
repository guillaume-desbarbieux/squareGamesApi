package com.guillaume.squareGamesApi.model;

import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("monFiltreDynamique")
public class SensorModel {
    private String name;
    private int id;
    private String secret;

    public SensorModel(int id, String name, String secret){
        this.id = id;
        this.name = name;
        this.secret = secret;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}