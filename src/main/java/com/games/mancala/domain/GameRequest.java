package com.games.mancala.domain;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameRequest {

    private String currentTurn;

    private int selectedPit;

    private String gameId;

}
