package com.guillaume.squareGamesApi.service;

public class SquareGameUnauthorizedException extends RuntimeException {
    public SquareGameUnauthorizedException(String message) {
        super(message);
    }
}
