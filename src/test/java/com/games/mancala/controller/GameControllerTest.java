package com.games.mancala.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.games.mancala.domain.GameDetails;
import com.games.mancala.domain.GameRequest;
import com.games.mancala.domain.Player;
import com.games.mancala.service.MancalaServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(GameController.class)
public class GameControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    MancalaServiceImpl mancalaServiceImpl;


    String gameId;

    @Before
    public void init() {
        gameId = UUID.randomUUID().toString();
    }

    @Test
    public void createGame() throws Exception {
        when(mancalaServiceImpl.createGame()).thenReturn(formGameDetails());
        mvc.perform(get("/newgame").contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("gameId").value(gameId))
                .andExpect(jsonPath("playerIdentifier").value("player1"))
                .andExpect(jsonPath("pits[6]").value(0));

    }

    @Test
    public void joinGame() throws Exception {
        when(mancalaServiceImpl.joinGame(anyString())).thenReturn(formGameDetails());
        mvc.perform(get("/joingame/" + gameId).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("gameId").value(gameId))
                .andExpect(jsonPath("playerIdentifier").value("player1"))
                .andExpect(jsonPath("pits[6]").value(0));

    }

    @Test
    public void refreshGame() throws Exception {
        when(mancalaServiceImpl.refreshGameDetails(anyString())).thenReturn(formRefreshState());
        mvc.perform(get("/refresh/" + gameId).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("nextPlayer").value("player2"))
                .andExpect(jsonPath("gameOver").value(false));

    }

    @Test
    public void execute() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        when(mancalaServiceImpl.executeCurrentMove(formGameRequest())).thenReturn(formRefreshState());
        System.out.println(formGameRequest().toString());
        mvc.perform(put("/execute")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(formGameRequest())))
                .andExpect(jsonPath("nextPlayer").value("player2"))
                .andExpect(jsonPath("gameOver").value(false));

    }

    private GameRequest formGameRequest() {
        return GameRequest.builder().selectedPit(3).gameId(gameId).currentTurn("player2").build();
    }

    private GameDetails formRefreshState() {
        return GameDetails.builder().gameOver(false).nextPlayer("player2").pits(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5}).build();
    }


    private GameDetails formGameDetails() {
        return GameDetails.builder().gameId(gameId).pits(new int[]{6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0}).playerIdentifier(Player.PLAYER_ONE.getPlayerIdentifier()).build();
    }
}
