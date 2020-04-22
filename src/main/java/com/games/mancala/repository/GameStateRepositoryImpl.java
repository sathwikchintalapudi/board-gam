package com.games.mancala.repository;

import com.games.mancala.domain.GameState;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

import static com.games.mancala.utils.MancalGameConstants.MANCALA_GAME_KEY;

@Repository
public class GameStateRepositoryImpl implements GameStateRepository{

    private RedisTemplate<String, GameState> template;

    private HashOperations hashOperations;

    public GameStateRepositoryImpl(RedisTemplate<String, GameState> template) {
        this.template = template;
        this.hashOperations = template.opsForHash();
    }

    public void save(String gameId, GameState gameState) {
        hashOperations.put(MANCALA_GAME_KEY, gameId, gameState);
    }

    public Map<String, GameState> findAll() {
        return hashOperations.entries(MANCALA_GAME_KEY);
    }


    public void update(String gameId, GameState gameState) {
         save(gameId, gameState);
    }

    public GameState getGameStateById(String gameId) {
        return (GameState) hashOperations.get(MANCALA_GAME_KEY, gameId);
    }

    public void deleteGame(String gameId) {
        hashOperations.delete(gameId);
    }
}
