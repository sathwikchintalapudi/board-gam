package com.games.mancala.domain;

public enum Player {

    PLAYER_ONE("player1", new int[]{0, 1, 2, 3, 4, 5}, 6),
    PLAYER_TWO("player2", new int[]{7, 8, 9, 10, 11, 12}, 13);

    private final String playerIdentifier;
    private final int[] playPits;
    private final int scorePit;


    Player(String playerIdentifier, int[] playPits, int scorePit) {
        this.playerIdentifier = playerIdentifier;
        this.playPits = playPits;
        this.scorePit = scorePit;
    }

    public String getPlayerIdentifier() {
        return playerIdentifier;
    }

    public int[] getPlayPits() {
        return playPits;
    }

    public int getScorePit() {
        return scorePit;
    }
}
