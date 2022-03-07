package com.game.kalah.service;

import com.game.kalah.dto.GameDTO;
import com.game.kalah.dto.PlayerDTO;
import com.game.kalah.model.Game;
import com.game.kalah.model.GameStatus;
import com.game.kalah.repository.GameRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class GameServiceTest {

    private GameRepository gameRepository;
    private GameService gameService;

    @BeforeEach
    void setUp() {
        gameRepository = Mockito.mock(GameRepository.class);
        gameService = new GameService(gameRepository);
    }

    @Test
    void createGame() {
        GameDTO gameDTO = GameDTO.builder()
                .firstPlayerName("Geroudo")
                .secondPlayerName("Abigobaldo")
                .build();

        Game build = Game.builder()
                .firstPlayerName(gameDTO.getFirstPlayerName())
                .secondPlayerName(gameDTO.getSecondPlayerName())
                .firstPlayerKey(UUID.randomUUID())
                .secondPlayerKey(UUID.randomUUID())
                .status(GameStatus.WAITING)
                .matchTime(0L)
                .cups(createBoard())
                .build();

        when(gameRepository.save(any()))
                .thenReturn(build);

        Game game = gameService.createGame(gameDTO);

        Mockito.verify(gameRepository, Mockito.times(1)).save(any());

        assertEquals(gameDTO.getFirstPlayerName(), game.getFirstPlayerName());
    }

    @Test
    void getGameFirstPlayer() {
        UUID firstPlayerKey = UUID.randomUUID();

        Game build = Game.builder()
                .firstPlayerName("Geroudo")
                .secondPlayerName("Asdrubal")
                .firstPlayerKey(firstPlayerKey)
                .secondPlayerKey(UUID.randomUUID())
                .status(GameStatus.WAITING)
                .matchTime(0L)
                .cups(createBoard())
                .build();

        when(gameRepository.findByFirstPlayerKey(firstPlayerKey))
                .thenReturn(Optional.ofNullable(build));

        Game game = gameService.getGame(firstPlayerKey.toString());

        Mockito.verify(gameRepository, Mockito.times(1)).findByFirstPlayerKey(firstPlayerKey);

        assertEquals(firstPlayerKey, game.getFirstPlayerKey());
    }

    @Test
    void getGameSecondPlayer() {
        UUID secondPlayerKey = UUID.randomUUID();

        Game build = Game.builder()
                .firstPlayerName("Geroudo")
                .secondPlayerName("Asdrubal")
                .firstPlayerKey(UUID.randomUUID())
                .secondPlayerKey(secondPlayerKey)
                .status(GameStatus.WAITING)
                .matchTime(0L)
                .cups(createBoard())
                .build();

        when(gameRepository.findByFirstPlayerKey(secondPlayerKey))
                .thenReturn(Optional.empty());

        when(gameRepository.findBySecondPlayerKey(secondPlayerKey))
                .thenReturn(Optional.ofNullable(build));

        Game game = gameService.getGame(secondPlayerKey.toString());

        Mockito.verify(gameRepository, Mockito.times(1)).findByFirstPlayerKey(secondPlayerKey);
        Mockito.verify(gameRepository, Mockito.times(1)).findBySecondPlayerKey(secondPlayerKey);

        assertEquals(secondPlayerKey, game.getSecondPlayerKey());
    }

    @Test
    void getGameUnknownPlayerError() {

        when(gameRepository.findByFirstPlayerKey(any()))
                .thenReturn(Optional.empty());

        when(gameRepository.findBySecondPlayerKey(any()))
                .thenReturn(Optional.empty());

        ResponseStatusException responseStatusException = assertThrows(
                ResponseStatusException.class, () -> gameService.getGame(UUID.randomUUID().toString()));

        Mockito.verify(gameRepository, Mockito.times(1)).findByFirstPlayerKey(any());
        Mockito.verify(gameRepository, Mockito.times(1)).findBySecondPlayerKey(any());

        assertTrue(Objects.requireNonNull(responseStatusException.getMessage()).contains("game.player_not_found"));
    }

    @Test
    void getPlayer() {
        UUID firstPlayerKey = UUID.randomUUID();

        Game build = Game.builder()
                .firstPlayerName("Geroudo")
                .secondPlayerName("Asdrubal")
                .firstPlayerKey(firstPlayerKey)
                .secondPlayerKey(UUID.randomUUID())
                .status(GameStatus.WAITING)
                .matchTime(0L)
                .cups(createBoard())
                .build();

        when(gameRepository.findByFirstPlayerKey(firstPlayerKey))
                .thenReturn(Optional.ofNullable(build));

        PlayerDTO player = gameService.getPlayer(firstPlayerKey.toString());

        Mockito.verify(gameRepository, Mockito.times(1)).findByFirstPlayerKey(firstPlayerKey);

        assertEquals(firstPlayerKey.toString(), player.getPlayerKey());
    }

    @Test
    void pingGame() {
        UUID uuid = UUID.randomUUID();
        Game game = Game.builder()
                .firstPlayerName("Artyom")
                .secondPlayerName("Zé")
                .firstPlayerKey(uuid)
                .secondPlayerKey(UUID.randomUUID())
                .status(GameStatus.WAITING)
                .matchTime(0L)
                .cups(createBoard())
                .build();

        when(gameRepository.findByFirstPlayerKey(uuid))
                .thenReturn(Optional.ofNullable(game));

        when(gameRepository.save(any()))
                .thenReturn(game);

        gameService.pingGame(uuid.toString());

        Mockito.verify(gameRepository, Mockito.times(1)).save(any());
    }

    @Test
    void updateGame() {
        Game game = Game.builder()
                .firstPlayerName("Artyom")
                .secondPlayerName("Zé")
                .firstPlayerKey(UUID.randomUUID())
                .secondPlayerKey(UUID.randomUUID())
                .status(GameStatus.WAITING)
                .matchTime(0L)
                .cups(createBoard())
                .build();

        when(gameRepository.save(any()))
                .thenReturn(game);

        gameService.updateGame(game);

        Mockito.verify(gameRepository, Mockito.times(1)).save(any());
    }

    private Map<Integer, Integer> createBoard() {
        Map<Integer, Integer> map = new HashMap<>();

        for(int i = 1; i <= 12; i++) {
            map.put(i, 4);
        }

        return map;
    }
}