package com.games.mancala.repository;

import com.games.mancala.domain.GameState;

import java.util.Map;

public interface GameStateRepository {


    /**
     * Save the gameState
     *
     * @param gameId, Unique gameId
     * @param gameState, Contains the game state like stones in each pit and next player eligible to update game state
     */
    void save(String gameId, GameState gameState);

    /**
     * gets all the gameStates from memory
     *
     */
    Map<String, GameState> findAll();

    /**
     * Updates the GameState for the given GameID
     *
     * @param gameId, Unique gameId
     * @param gameState, Contains the game state like stones in each pit and next player eligible to update game state
     */
    void update(String gameId, GameState gameState);

    /**
     *
     * @param gameId, Unique gameId
     * @return gameState, Contains the game state like stones in each pit and next player eligible to update game state
     */
    GameState getGameStateById(String gameId);

    /**
     * Deletes the game with specified gameId from the memory
     * @param gameId, Unique gameId
     */
    void deleteGame(String gameId);

}
