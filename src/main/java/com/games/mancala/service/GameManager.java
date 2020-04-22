package com.games.mancala.service;

import com.games.mancala.domain.GameState;
import com.games.mancala.exceptions.MancalaGameException;
import com.games.mancala.repository.GameStateRepository;
import com.games.mancala.repository.GameStateRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static com.games.mancala.utils.MancalGameConstants.DB_CONN_ERR_CODE;
import static com.games.mancala.utils.MancalGameConstants.INVALID_GAME_ID;
import static com.games.mancala.utils.MancalGameConstants.INVALID_INIT_ERR_CODE;

@Service
public class GameManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameManager.class);
    private final GameStateRepository gameStateRepository;

    public GameManager(GameStateRepositoryImpl gameStateRepository) {
        this.gameStateRepository = gameStateRepository;
    }

    /**
     * Creates a new game for the given gameId and persists in memory
     *
     * @param gameId, Unique gameId
     * @return GameState, Contains the game state like stones in each pit and next player eligible to update game state
     */
    GameState createGame(String gameId) {
        GameState newGame = new GameState();
        try {
            gameStateRepository.save(gameId, newGame);
            newGame = gameStateRepository.getGameStateById(gameId);
        } catch (Exception exp) {
            LOGGER.error("Exception occurred while saving new game to redis", exp);
            throw new MancalaGameException("Unable to create game please try again later", DB_CONN_ERR_CODE);
        }
        return newGame;
    }

    /**
     * Validates the given gameId and starts game for that game by updating game started flag to true
     *
     * @param gameId, Unique gameId
     * @return GameState, Contains the game state like stones in each pit and next player eligible to update game state
     */
    GameState joinGame(String gameId) {
        GameState gameState;
        try {
            gameState = gameStateRepository.getGameStateById(gameId);

            if (gameState == null) {
                throw new MancalaGameException("Please kindly enter valid gameId", INVALID_GAME_ID);
            }
            if (gameState.isGameStarted()) {
                throw new MancalaGameException("Game already started for given gameId, kindly create a new game", INVALID_INIT_ERR_CODE);
            }
            gameState.setGameStarted(true);
            gameStateRepository.update(gameId, gameState);
        } catch (MancalaGameException mexp) {
            LOGGER.error("Exception occurred while player joining game to redis : {}", mexp.getMessage());
            throw mexp;
        } catch (Exception exp) {
            LOGGER.error("Exception occurred while player joining game to redis", exp);
            throw new MancalaGameException("Unable to connect to requested game please try again later", DB_CONN_ERR_CODE);
        }
        return gameState;
    }

    /**
     * Gets the game state info for the mentioned gameId
     *
     * @param gameId, Unique gameId
     * @return GameState, Contains the game state like stones in each pit and next player eligible to update game state
     */
    GameState getGameStateById(String gameId) {
        GameState gameState;
        try {
            gameState = gameStateRepository.getGameStateById(gameId);
        } catch (Exception exp) {
            LOGGER.error("Exception occurred while while retrieving gameState", exp);
            throw new MancalaGameException("Unable to refresh game with requested Id please kindly try again later", DB_CONN_ERR_CODE);
        }
        return gameState;
    }

    /**
     * Updates the gameState for the mentioned gameId
     *
     * @param gameId, Unique gameId
     * @param gameState, Updated gameState after executing players selection
     */
    void updateGameStateById(String gameId, GameState gameState) {
        try {
            gameStateRepository.update(gameId, gameState);
        } catch (Exception exp) {
            LOGGER.error("Exception occurred while while updating gameState", exp);
            throw new MancalaGameException("Unable to execute selected step", DB_CONN_ERR_CODE);
        }
    }
}
