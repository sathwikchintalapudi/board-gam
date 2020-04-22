package com.games.mancala.service;

import com.games.mancala.domain.GameRequest;
import com.games.mancala.domain.GameState;
import com.games.mancala.exceptions.MancalaGameException;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static com.games.mancala.utils.MancalGameConstants.INVALID_INIT_ERR_CODE;
import static com.games.mancala.utils.MancalGameConstants.INVALID_PIT_SELECTION_ERR_CODE;
import static com.games.mancala.utils.MancalGameConstants.INVALID_PLAYER_ACTION_ERR_CODE;

@Service
public class Validator {

    /**
     * Validates the current selection
     * Current selection is invalid if
     * 1. game is not stated i.e both players did joined the game
     * 2. Player selects opponent players pits
     * 3. Player is not assigned to play turn
     * 4. Player selects score pit
     * 5. If a player selects an empty pit
     *
     * @param gameState, Contains the game state like stones in each pit and next player eligible to update game state
     * @param gameRequest, Contains currentSelection info, current player info
     */
    public void validateCurrentMove(GameState gameState, GameRequest gameRequest) {

        //Game not started exception
        if (!gameState.isGameStarted()) {
            throw new MancalaGameException("Need one more player to start game", INVALID_INIT_ERR_CODE);
        }

        //Invalid player action
        if (!gameRequest.getCurrentTurn().equalsIgnoreCase(gameState.getCurrentPlayer().getPlayerIdentifier())) {
            throw new MancalaGameException(gameState.getCurrentPlayer().getPlayerIdentifier() + " did not play his turn", INVALID_PLAYER_ACTION_ERR_CODE);
        }

        //Opponent player pits selection exception
        if (Arrays.stream(gameState.getOpponentPlayer().getPlayPits()).anyMatch(a -> (a == gameRequest.getSelectedPit()))) {
            throw new MancalaGameException("Can not select other player pits", INVALID_PIT_SELECTION_ERR_CODE);
        }

        //Score pit selection exception
        if (gameState.getCurrentPlayer().getScorePit() == gameRequest.getSelectedPit()) {
            throw new MancalaGameException("Can not select score pit", INVALID_PIT_SELECTION_ERR_CODE);
        }

        //Empty Selection pit
        if (gameState.getPits()[gameRequest.getSelectedPit()] == 0) {
            throw new MancalaGameException("Empty pit can not be selected", INVALID_PIT_SELECTION_ERR_CODE);
        }
    }
}
