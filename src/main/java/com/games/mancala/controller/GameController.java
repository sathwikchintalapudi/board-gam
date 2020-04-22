package com.games.mancala.controller;

import com.games.mancala.domain.GameDetails;
import com.games.mancala.domain.GameRequest;
import com.games.mancala.service.MancalaServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {

    private final MancalaServiceImpl mancalaServiceImpl;

    public GameController(MancalaServiceImpl mancalaServiceImpl) {
        this.mancalaServiceImpl = mancalaServiceImpl;
    }

    /**
     * Creates a new game
     *
     * @return GameDetails, contains player info and pits info
     */
    @PostMapping(value = "/newgame")
    public GameDetails createGame() {
        return mancalaServiceImpl.createGame();
    }

    /**
     * Adds second player to game with specified gameId and
     * starts the game allowing players to select pits
     *
     * @param gameId, unique Id given for a game
     * @return GameDetails, contains player info and pits info
     */
    @GetMapping(value = "/joingame/{gameId}")
    public GameDetails joinGame(@PathVariable String gameId) {
        return mancalaServiceImpl.joinGame(gameId);
    }

    /**
     * Gets the updated status of game with specified gameId
     *
     * @param gameId, unique Id given for a game
     * @return GameDetails, contains player info and pits info
     */
    @GetMapping(value = "/refresh/{gameId}")
    public GameDetails refreshGameDetails(@PathVariable String gameId) {
        return mancalaServiceImpl.refreshGameDetails(gameId);
    }

    /**
     * Executes the step
     *
     * @param gameRequest, contained the selected pit info and current player info
     * @return GameDetails, contains player info and pits info
     */
    @PutMapping(value = "/execute")
    public GameDetails execute(@RequestBody GameRequest gameRequest) {
        return mancalaServiceImpl.executeCurrentMove(gameRequest);
    }

}
