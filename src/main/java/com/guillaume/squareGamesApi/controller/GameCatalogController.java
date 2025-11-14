package com.guillaume.squareGamesApi.controller;

import com.guillaume.squareGamesApi.service.GameCatalog;
import org.springframework.web.bind.annotation.*;
import java.util.Collection;


@RestController
@RequestMapping("games")
public class GameCatalogController {

    GameCatalog gameCatalog;
    public GameCatalogController(GameCatalog gameCatalog) {
        this.gameCatalog = gameCatalog;
    }

    @GetMapping()
    public Collection<String> getGameIdentifiers() {
        return gameCatalog.getGameIdentifiers();
    }
}
