package com.games.mancala.service;

import com.games.mancala.domain.GameDetails;
import com.games.mancala.domain.GameRequest;
import com.games.mancala.domain.GameState;
import com.games.mancala.domain.Player;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;

@Service
public class MancalaServiceImpl {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MancalaServiceImpl.class);
    private final Validator validator;
    private final GameManager gameManager;


    public MancalaServiceImpl(Validator validator, GameManager gameManager) {
        this.validator = validator;
        this.gameManager = gameManager;
    }

    /**
     * Creates a new game
     *
     * @return GameDetails, contains player info and pits info
     */
    public GameDetails createGame() {
        String gameId = UUID.randomUUID().toString();
        GameState gameState = gameManager.createGame(gameId);
        return GameDetails.builder()
                .gameId(gameId)
                .pits(gameState.getPits())
                .playerIdentifier(gameState.getCurrentPlayer().getPlayerIdentifier())
                .build();
    }

    /**
     * Adds second player to game with specified gameId and
     * starts the game allowing players to select pits
     *
     * @param gameId, unique Id given for a game
     * @return GameDetails, contains player info and pits info
     */
    public GameDetails joinGame(String gameId) {
        GameState gameState = gameManager.joinGame(gameId);
        return GameDetails.builder()
                .gameId(gameId)
                .pits(gameState.getPits())
                .nextPlayer(gameState.getCurrentPlayer().getPlayerIdentifier())
                .playerIdentifier(gameState.getOpponentPlayer().getPlayerIdentifier())
                .build();
    }

    /**
     * Gets the updated status of game of mentioned gameId
     *
     * @param gameId, unique Id given for a game
     * @return GameDetails, contains players info and pits info
     */
    public GameDetails refreshGameDetails(String gameId) {
        GameState gameState = gameManager.getGameStateById(gameId);
        return GameDetails.builder()
                .gameId(gameId)
                .pits(gameState.getPits())
                .nextPlayer(gameState.getCurrentPlayer().getPlayerIdentifier())
                .gameOver(gameState.isGameOver())
                .winner(gameState.getWinner())
                .build();
    }


    /**
     * validates and executes the current selection
     * executes :
     * 1. Updates the stones in the game board
     * 2. Checks if the opponent players stones can be captured
     * 3. Checks if the gameOver condition is met
     * 4. Finds the next player eligible to play
     *
     * @param gameRequest, Contains the currentPlayers current selection
     * @return GameDetails, contains players info and pits info
     */
    public GameDetails executeCurrentMove(GameRequest gameRequest) {
        LOGGER.info("Player: {} selected pit number: {}", gameRequest.getCurrentTurn(), gameRequest.getSelectedPit());
        GameState gameState = gameManager.getGameStateById(gameRequest.getGameId());
        LOGGER.info("Game state before execution {}", gameState.toString());
        validator.validateCurrentMove(gameState, gameRequest);

        int[] pits = gameState.getPits();
        updateBoard(gameRequest.getSelectedPit(), gameState);
        if (pits[gameState.getLastSowedPit()] == 1 && Arrays.stream(gameState.getCurrentPlayer().getPlayPits()).anyMatch(a -> (a == gameState.getLastSowedPit()))) {
            captureOpponentsStones(pits, gameState.getCurrentPlayer(), gameState.getLastSowedPit());
        }
        analyzeMove(gameState);
        gameManager.updateGameStateById(gameRequest.getGameId(), gameState);
        LOGGER.info("Game state after execution {}", gameState.toString());

        return GameDetails.builder()
                .gameId(gameRequest.getGameId())
                .pits(gameState.getPits())
                .nextPlayer(gameState.getCurrentPlayer().getPlayerIdentifier())
                .gameOver(gameState.isGameOver())
                .winner(gameState.getWinner())
                .build();

    }

    private void analyzeMove(GameState gameState) {
        Player currentPlayer = gameState.getCurrentPlayer();
        Player opponentPlayer = gameState.getOpponentPlayer();
        if (gameState.isCurrentPlayerPitsEmpty()) {
            gameState.setGameOver(true);
            clearPlayersPits(gameState.getPits(), opponentPlayer.getScorePit(), opponentPlayer.getPlayPits());
            gameState.determineWinner();
        } else if (gameState.isOpponentPlayerPitsEmpty()) {
            gameState.setGameOver(true);
            clearPlayersPits(gameState.getPits(), currentPlayer.getScorePit(), currentPlayer.getPlayPits());
            gameState.determineWinner();
        } else if (gameState.getLastSowedPit() != currentPlayer.getScorePit()) {
            gameState.updateNextTurn(gameState.getOpponentPlayer());
        }
    }

    private void updateBoard(int selectedPit, GameState gameState) {
        int[] pits = gameState.getPits();
        int nextPit = 0;
        int stonesFetched = pits[selectedPit];
        pits[selectedPit] = 0;
        for (int i = 1; i <= stonesFetched; i++) {
            nextPit = (i + selectedPit) % pits.length;
            pits[nextPit] = pits[nextPit] + 1;
            if (nextPit == gameState.getOpponentPlayer().getScorePit()) {
                pits[nextPit] = pits[nextPit] - 1;
                stonesFetched++;
            }
        }
        gameState.setLastSowedPit(nextPit);
    }

    private void captureOpponentsStones(int[] pits, Player currentPlayer, int lastSowedPit) {
        int opponentStones = pits[12 - lastSowedPit];
        pits[12 - lastSowedPit] = 0;
        pits[lastSowedPit] = 0;
        pits[currentPlayer.getScorePit()] = pits[currentPlayer.getScorePit()] + opponentStones + 1;
    }

    private void clearPlayersPits(int[] board, int scorePit, int[] validPits) {
        int sum = 0;
        for (int playerPit : validPits) {
            sum = sum + board[playerPit];
            board[playerPit] = 0;
        }
        board[scorePit] = board[scorePit] + sum;
    }

}
