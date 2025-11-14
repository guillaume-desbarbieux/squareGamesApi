package com.guillaume.squareGamesApi.service;

import fr.le_campus_numerique.square_games.engine.connectfour.ConnectFourGameFactory;
import fr.le_campus_numerique.square_games.engine.taquin.TaquinGameFactory;
import fr.le_campus_numerique.square_games.engine.tictactoe.TicTacToeGameFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class GameCatalogImpl implements GameCatalog {

    @Autowired
    TicTacToeGameFactory ticTacToeGameFactory;

    @Autowired
    ConnectFourGameFactory connectFourGameFactory;

    @Autowired
    TaquinGameFactory taquinGameFactory;

    @Override
    public Collection<String> getGameIdentifiers() {
        return List.of(
                ticTacToeGameFactory.getGameFactoryId(),
                connectFourGameFactory.getGameFactoryId(),
                taquinGameFactory.getGameFactoryId());
    }
}
