package com.games.mancala.domain;

import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.Arrays;

import static com.games.mancala.utils.MancalGameConstants.INITIAL_GAME_BOARD;

@Data
@RedisHash
public class GameState implements Serializable {

    private Player currentPlayer;

    private Player opponentPlayer;

    private int[] pits;

    private boolean gameOver;

    private String winner;

    private boolean gameStarted;

    private int lastSowedPit;

    public GameState() {
        this.pits = INITIAL_GAME_BOARD;
        currentPlayer = Player.PLAYER_ONE;
        opponentPlayer = Player.PLAYER_TWO;
    }

    public boolean isCurrentPlayerPitsEmpty() {
        return Arrays.stream(currentPlayer.getPlayPits()).noneMatch(a -> pits[a] != 0);
    }

    public boolean isOpponentPlayerPitsEmpty() {
        return Arrays.stream(opponentPlayer.getPlayPits()).noneMatch(a -> pits[a] != 0);
    }


    public void determineWinner() {
        if (pits[this.currentPlayer.getScorePit()] == pits[this.opponentPlayer.getScorePit()]) {
            this.winner = "GAME DRAW";
        } else {
            this.winner = pits[this.currentPlayer.getScorePit()] > pits[this.opponentPlayer.getScorePit()] ? this.currentPlayer.getPlayerIdentifier() : this.opponentPlayer.getPlayerIdentifier();
        }
    }

    public void updateNextTurn(Player nextTurn) {
        this.currentPlayer = nextTurn;
        this.opponentPlayer = nextTurn.getPlayerIdentifier().equalsIgnoreCase("player1") ? Player.PLAYER_TWO : Player.PLAYER_ONE;
    }

    @Override
    public String toString() {
        return "GameState{" +
                "currentPlayer=" + currentPlayer +
                ", opponentPlayer=" + opponentPlayer +
                ", pits=" + Arrays.toString(pits) +
                ", gameOver=" + gameOver +
                ", winner='" + winner + '\'' +
                ", gameStarted=" + gameStarted +
                ", lastSowedPit=" + lastSowedPit +
                '}';
    }
}
