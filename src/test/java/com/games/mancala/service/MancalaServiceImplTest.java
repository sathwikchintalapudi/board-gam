package com.games.mancala.service;

import com.games.mancala.domain.GameDetails;
import com.games.mancala.domain.GameRequest;
import com.games.mancala.domain.GameState;
import com.games.mancala.domain.Player;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MancalaServiceImplTest {

    @Mock
    Validator validator;

    @Mock
    GameManager gameManager;

    @InjectMocks
    MancalaServiceImpl mancalaService;

    private String gameId;

    @Before
    public void init() {
        gameId = UUID.randomUUID().toString();
    }

    @Test
    public void createGame() {
        when(gameManager.createGame(anyString())).thenReturn(formGameState());
        GameDetails gameDetails = mancalaService.createGame();
        assertNotNull("GameId is not returned", gameDetails.getGameId());
        assertTrue("Initial Game board pits are not as expected", compareArray(gameDetails.getPits(), new int[]{6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0}));
        assertEquals("Initial current player info is not as expected", "player1", gameDetails.getPlayerIdentifier());
    }

    @Test
    public void joinGame() {
        when(gameManager.joinGame(anyString())).thenReturn(formGameState());
        GameDetails gameDetails = mancalaService.joinGame(gameId);
        assertNotNull("GameId is not returned", gameDetails.getGameId());
        assertTrue("Initial Game board pits are not as expected", compareArray(gameDetails.getPits(), new int[]{6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0}));
        assertEquals("Initial opponent player info is not as expected", "player2", gameDetails.getPlayerIdentifier());
    }

    @Test
    public void refreshGameDetails() {
        GameState gameState = formGameState();
        gameState.setPits(new int[]{1, 2, 3, 4, 5, 6, 7, 1, 1, 1, 1, 1, 1, 10});
        when(gameManager.getGameStateById(anyString())).thenReturn(gameState);
        GameDetails refreshGameDetails = mancalaService.refreshGameDetails(gameId);
        assertEquals("GameBoard is not returned", "player1", refreshGameDetails.getNextPlayer());
        assertTrue("Game board pits are not as expected", compareArray(refreshGameDetails.getPits(), new int[]{1, 2, 3, 4, 5, 6, 7, 1, 1, 1, 1, 1, 1, 10}));
        assertFalse("GameOver status is not returned as expected", refreshGameDetails.isGameOver());
    }


    @Test
    public void executeCurrentMove() {
        GameState gameState = formGameState();
        when(gameManager.getGameStateById(anyString())).thenReturn(gameState);
        GameDetails refreshState = mancalaService.executeCurrentMove(formGameRequest("player1",3));
        assertEquals("player info is not as expected", "player2", refreshState.getNextPlayer());
        assertNull("Winner info is not as expected", refreshState.getWinner());
        assertTrue("Pits in game board are not as expected", compareArray(refreshState.getPits(), new int[]{6, 6, 6, 0, 7, 7, 1, 7, 7, 7, 6, 6, 6, 0}));
    }

    @Test
    public void executeCurrentMove_ExtraTurn() {
        GameState gameState = formGameState();
        when(gameManager.getGameStateById(anyString())).thenReturn(gameState);
        GameDetails refreshState = mancalaService.executeCurrentMove(formGameRequest("player1",0));
        assertEquals("player info is not as expected", "player1", refreshState.getNextPlayer());
        assertNull("Winner info is not as expected", refreshState.getWinner());
        assertTrue("Pits in game board are not as expected", compareArray(refreshState.getPits(), new int[]{0, 7, 7, 7, 7, 7, 1, 6, 6, 6, 6, 6, 6, 0}));
    }

    @Test
    public void executeCurrentMove_captureOpponentsStones() {
        GameState gameState = formGameState();
        gameState.setPits(new int[]{2, 1, 0, 7, 7, 7, 1, 6, 6, 6, 6, 6, 6, 0});
        when(gameManager.getGameStateById(anyString())).thenReturn(gameState);
        GameDetails refreshState = mancalaService.executeCurrentMove(formGameRequest("player1",0));
        assertEquals("player info is not as expected", "player2", refreshState.getNextPlayer());
        assertNull("Winner info is not as expected", refreshState.getWinner());
        assertTrue("Pits in game board are not as expected", compareArray(refreshState.getPits(), new int[]{0, 2, 0, 7, 7, 7, 8, 6, 6, 6, 0, 6, 6, 0}));
    }

    @Test
    public void executeCurrentMove_gameover_currentPlayer() {
        GameState gameState = formGameState();
        gameState.setPits(new int[]{0, 0, 0, 0, 0, 1, 16, 4, 5, 7, 1, 1, 1, 0});
        when(gameManager.getGameStateById(anyString())).thenReturn(gameState);
        GameDetails refreshState = mancalaService.executeCurrentMove(formGameRequest("player1",5));
        assertEquals("Winner info is not as expected", "player2",refreshState.getWinner());
        assertTrue("Game over info is not as expected", refreshState.isGameOver());
        assertTrue("Pits in game board are not as expected", compareArray(refreshState.getPits(), new int[]{0, 0, 0, 0, 0, 0, 17, 0, 0, 0, 0, 0, 0, 19}));
    }


    @Test
    public void executeCurrentMove_captureOpponentsStones_GameOver() {
        GameState gameState = formGameState();
        gameState.setPits(new int[]{2, 1, 0, 7, 7, 7, 1, 0, 0, 0, 6, 0, 0, 15});
        when(gameManager.getGameStateById(anyString())).thenReturn(gameState);
        GameDetails refreshState = mancalaService.executeCurrentMove(formGameRequest("player1",0));
        assertEquals("Winner info is not as expected", "player1",refreshState.getWinner());
        assertTrue("Game over info is not as expected", refreshState.isGameOver());
        assertTrue("Pits in game board are not as expected", compareArray(refreshState.getPits(), new int[]{0, 0, 0, 0, 0, 0, 31, 0, 0, 0, 0, 0, 0, 15}));
    }

    private GameRequest formGameRequest(String playerInfo, int selectedPit) {
        return GameRequest.builder().gameId(gameId).selectedPit(selectedPit).currentTurn(playerInfo).build();
    }


    private GameState formGameState() {
        GameState gameState = new GameState();
        gameState.setGameStarted(false);
        gameState.setOpponentPlayer(Player.PLAYER_TWO);
        gameState.setCurrentPlayer(Player.PLAYER_ONE);
        gameState.setPits(new int[]{6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0});
        return gameState;
    }

    private boolean compareArray(int[] playPits, int[] playPits1) {
        boolean isEqual = true;
        if (playPits.length == playPits1.length) {
            for (int i = 0; i < playPits.length; i++) {
                if (playPits[i] != playPits1[i]) {
                    isEqual = false;
                    break;
                }
            }
        }
        return isEqual;
    }

}
