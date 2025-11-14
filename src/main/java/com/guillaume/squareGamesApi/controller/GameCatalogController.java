package com.guillaume.squareGamesApi.controller;

import com.guillaume.squareGamesApi.service.GameCatalog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class GameCatalogController {

    @Autowired
    GameCatalog gameCatalog;

    @GetMapping ("catalog")
    public Collection<String> getGameIdentifiers(){
        return gameCatalog.getGameIdentifiers();
    }
}
