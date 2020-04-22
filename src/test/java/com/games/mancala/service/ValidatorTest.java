package com.games.mancala.service;


import com.games.mancala.domain.GameRequest;
import com.games.mancala.domain.GameState;
import com.games.mancala.domain.Player;
import com.games.mancala.exceptions.MancalaGameException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
public class ValidatorTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    Validator validator;
    String gameId;

    @Before
    public void init() {
        validator = new Validator();
        gameId = UUID.randomUUID().toString();
    }

    @Test
    public void validateCurrentMove_GameNotStartedException() {
        GameState gameState = formGameState();
        GameRequest gameRequest = formGameRequest();
        gameState.setGameStarted(false);

        expectedException.expectMessage("Need one more player to start game");
        expectedException.expect(MancalaGameException.class);
        validator.validateCurrentMove(gameState, gameRequest);
    }

    @Test
    public void validateCurrentMove_InvalidPlayerException() {
        GameState gameState = formGameState();
        GameRequest gameRequest = formGameRequest();
        gameRequest.setCurrentTurn("player2");

        expectedException.expectMessage("player1 did not play his turn");
        expectedException.expect(MancalaGameException.class);
        validator.validateCurrentMove(gameState, gameRequest);
    }

    @Test
    public void validateCurrentMove_InvalidSelection() {
        GameState gameState = formGameState();
        GameRequest gameRequest = formGameRequest();
        gameRequest.setSelectedPit(9);
        expectedException.expectMessage("Can not select other player pits");
        expectedException.expect(MancalaGameException.class);
        validator.validateCurrentMove(gameState, gameRequest);
    }

    @Test
    public void validateCurrentMove_ScoreSelectionPit() {
        GameState gameState = formGameState();
        GameRequest gameRequest = formGameRequest();
        gameRequest.setSelectedPit(6);
        expectedException.expectMessage("Can not select score pit");
        expectedException.expect(MancalaGameException.class);
        validator.validateCurrentMove(gameState, gameRequest);
    }

    @Test
    public void validateCurrentMove_EmptyPitSelectionException() {
        GameState gameState = formGameState();
        GameRequest gameRequest = formGameRequest();
        int[] pits = gameState.getPits();
        pits[3] = 0;
        gameRequest.setSelectedPit(3);
        expectedException.expectMessage("Empty pit can not be selected");
        expectedException.expect(MancalaGameException.class);
        validator.validateCurrentMove(gameState, gameRequest);
    }

    private GameRequest formGameRequest() {
        return GameRequest.builder().gameId(gameId).currentTurn("player1").selectedPit(3).build();
    }

    private GameState formGameState() {
        GameState gameState = new GameState();
        gameState.setCurrentPlayer(Player.PLAYER_ONE);
        gameState.setOpponentPlayer(Player.PLAYER_TWO);
        gameState.setLastSowedPit(11);
        gameState.setGameStarted(true);
        gameState.setWinner(null);
        return gameState;
    }


}
