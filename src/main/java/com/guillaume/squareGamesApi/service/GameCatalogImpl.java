package com.guillaume.squareGamesApi.service;

import fr.le_campus_numerique.square_games.engine.GameFactory;
import fr.le_campus_numerique.square_games.engine.connectfour.ConnectFourGameFactory;
import fr.le_campus_numerique.square_games.engine.taquin.TaquinGameFactory;
import fr.le_campus_numerique.square_games.engine.tictactoe.TicTacToeGameFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class GameCatalogImpl implements GameCatalog {
    Collection<GameFactory> gameFactories;

    public GameCatalogImpl() {
        gameFactories = new ArrayList<>();
        gameFactories.add(new TicTacToeGameFactory());
        gameFactories.add(new ConnectFourGameFactory());
        gameFactories.add(new TaquinGameFactory());
    }

    @Override
    public Collection<String> getGameIdentifiers() {
        Collection<String> gameIdentifiers = new ArrayList<>();

        for (GameFactory factory : gameFactories)
            gameIdentifiers.add(factory.getGameFactoryId());

        return gameIdentifiers;
    }
}
