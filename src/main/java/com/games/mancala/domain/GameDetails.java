package com.games.mancala.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameDetails {

    private String gameId;

    private int[] pits;

    private String playerIdentifier;

    private String nextPlayer;

    private boolean gameOver;

    private String winner;

}
