package com.games.mancala.repository;

import com.games.mancala.domain.GameState;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GameStateRepositoryImplTest {

    @Mock
    HashOperations hashOperations;

    @Mock
    RedisTemplate<String, GameState> template;

    private String gameId;

    private GameState gameState;

    private GameStateRepositoryImpl gameStateRepository;

    @Before
    public void init() {
        when(template.opsForHash()).thenReturn(hashOperations);
        gameStateRepository = new GameStateRepositoryImpl(template);
        gameId = UUID.randomUUID().toString();
        gameState = new GameState();
    }

    @Test
    public void save() {
        gameStateRepository.save(gameId, gameState);
        verify(hashOperations, times(1)).put(anyString(), anyString(), any(GameState.class));
    }

    @Test
    public void findAll() {
        gameStateRepository.findAll();
        verify(hashOperations, times(1)).entries(anyString());
    }

    @Test
    public void update() {
        gameStateRepository.update(gameId, gameState);
        verify(hashOperations, times(1)).put(anyString(), anyString(), any(GameState.class));
    }

    @Test
    public void getGameStateById() {
        gameStateRepository.getGameStateById(gameId);
        verify(hashOperations, times(1)).get(anyString(), anyString());
    }

    @Test
    public void deleteGame() {
        gameStateRepository.deleteGame(gameId);
        verify(hashOperations, times(1)).delete(anyString());
    }

}
