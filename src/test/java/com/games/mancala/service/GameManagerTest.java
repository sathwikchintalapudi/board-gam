package com.games.mancala.service;

import com.games.mancala.domain.GameState;
import com.games.mancala.domain.Player;
import com.games.mancala.exceptions.MancalaGameException;
import com.games.mancala.repository.GameStateRepositoryImpl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import redis.clients.jedis.exceptions.JedisException;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GameManagerTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private GameStateRepositoryImpl gameStateRepository;

    @InjectMocks
    private GameManager gameManager;

    public String gameId;

    @Before
    public void init() {
        gameId = UUID.randomUUID().toString();
    }


    @Test
    public void createGame() {
        when(gameStateRepository.getGameStateById(anyString())).thenReturn(formGameState());
        GameState gameState = gameManager.createGame(gameId);
        assertEquals("current player is not as expected", "player1", gameState.getCurrentPlayer().getPlayerIdentifier());
        assertEquals("opponent player is not as expected", "player2", gameState.getOpponentPlayer().getPlayerIdentifier());

        assertEquals("current player score pit is not as expected", 6, gameState.getCurrentPlayer().getScorePit());
        assertEquals("opponent player is not as expected", 13, gameState.getOpponentPlayer().getScorePit());

        assertFalse("opponent player is not as expected", gameState.isGameOver());
        assertNull("winner info is not as expected", gameState.getWinner());

        assertTrue("opponent player play pits are not as expected", compareArray(gameState.getOpponentPlayer().getPlayPits(), Player.PLAYER_TWO.getPlayPits()));
        assertTrue("current player play pits are not as expected", compareArray(gameState.getCurrentPlayer().getPlayPits(), Player.PLAYER_ONE.getPlayPits()));

    }

    @Test
    public void createGame_Exception() {
        when(gameStateRepository.getGameStateById(anyString())).thenThrow(new JedisException("Unable to connect to server"));
        expectedException.expectMessage("Unable to create game please try again later");
        expectedException.expect(MancalaGameException.class);
        GameState gameState = gameManager.createGame(gameId);
    }

    @Test
    public void joinGame_MancalaException() {
        when(gameStateRepository.getGameStateById(anyString())).thenReturn(null);
        expectedException.expectMessage("Please kindly enter valid gameId");
        expectedException.expect(MancalaGameException.class);
        GameState gameState = gameManager.joinGame(gameId);
    }

    @Test
    public void joinGame_MancalaException_GameStarted() {
        GameState gameState1 = formGameState();
        gameState1.setGameStarted(true);
        when(gameStateRepository.getGameStateById(anyString())).thenReturn(gameState1);
        expectedException.expectMessage("Game already started for given gameId, kindly create a new game");
        expectedException.expect(MancalaGameException.class);
        GameState gameState = gameManager.joinGame(gameId);
        assertEquals("Game state is not as expected", "player1", gameState.getCurrentPlayer().getPlayerIdentifier());
    }

    @Test
    public void joinGame_MancalaException_Exception() {
        GameState gameState1 = formGameState();
        gameState1.setGameStarted(true);
        when(gameStateRepository.getGameStateById(anyString())).thenThrow(new JedisException("Unable to connect to server"));
        expectedException.expectMessage("Unable to connect to requested game please try again later");
        expectedException.expect(MancalaGameException.class);
        GameState gameState = gameManager.joinGame(gameId);
    }

    @Test
    public void joinGame() {
        when(gameStateRepository.getGameStateById(anyString())).thenReturn(formGameState());
        GameState gameState = gameManager.joinGame(gameId);
        assertEquals("Game state is not as expected", "player1", gameState.getCurrentPlayer().getPlayerIdentifier());
    }

    @Test
    public void getGameStateById() {
        when(gameStateRepository.getGameStateById(anyString())).thenReturn(formGameState());
        GameState gameState = gameManager.getGameStateById(gameId);
        assertEquals("Game state is not as expected", "player1", gameState.getCurrentPlayer().getPlayerIdentifier());
    }

    @Test
    public void getGameStateById_Exception() {
        when(gameStateRepository.getGameStateById(anyString())).thenThrow(new JedisException("Unable to connect to server"));
        expectedException.expectMessage("Unable to refresh game with requested Id please kindly try again later");
        expectedException.expect(MancalaGameException.class);
        GameState gameState = gameManager.getGameStateById(gameId);
    }

    @Test
    public void updateGameStateById() {
        gameManager.updateGameStateById(gameId,formGameState());
        verify(gameStateRepository, times(1)).update(anyString(),any(GameState.class));
    }

    private GameState formGameState() {
        GameState gameState = new GameState();
        gameState.setPits(new int[]{6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0});
        gameState.setCurrentPlayer(Player.PLAYER_ONE);
        gameState.setOpponentPlayer(Player.PLAYER_TWO);
        gameState.setGameStarted(false);
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
