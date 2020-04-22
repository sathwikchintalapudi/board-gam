package com.games.mancala.service;


import com.games.mancala.domain.GameRequest;
import com.games.mancala.domain.GameState;

@FunctionalInterface
public interface FunctionalInt {

    void validate(GameState gameState, GameRequest gameRequest);

}
