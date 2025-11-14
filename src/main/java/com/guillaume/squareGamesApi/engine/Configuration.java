package com.guillaume.squareGamesApi.engine;

import fr.le_campus_numerique.square_games.engine.connectfour.ConnectFourGameFactory;
import fr.le_campus_numerique.square_games.engine.taquin.TaquinGameFactory;
import fr.le_campus_numerique.square_games.engine.tictactoe.TicTacToeGameFactory;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    public TicTacToeGameFactory ticTacToeGameFactory() {
        return new TicTacToeGameFactory();
    }

    @Bean
    public ConnectFourGameFactory connectFourGameFactory() {
        return new ConnectFourGameFactory();
    }

    @Bean
    public TaquinGameFactory taquinGameFactory() {
        return new TaquinGameFactory();
    }

}
