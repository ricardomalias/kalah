package com.game.kalah.service;

import com.game.kalah.dto.MoveDTO;
import com.game.kalah.dto.PlayerDTO;
import com.game.kalah.model.Game;
import com.game.kalah.model.GameStatus;
import com.game.kalah.util.MessageUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class RoundServiceTest {

    private GameService gameService;
    private RoundService roundService;

    private Game gameMock;

    @BeforeEach
    void setUp() {
        gameService = Mockito.mock(GameService.class);
        MessageUtil messageUtil = Mockito.mock(MessageUtil.class);
        roundService = new RoundService(gameService, messageUtil);

        gameMock = Game.builder()
                .idGame("123")
                .firstPlayerName("Asdrubal")
                .secondPlayerName("Astolfo")
                .firstPlayerKey(UUID.randomUUID())
                .secondPlayerKey(UUID.randomUUID())
                .status(GameStatus.WAITING)
                .matchTime(0L)
                .cups(createBoard())
                .build();
    }

    @Test
    void moveFirstPlayerSuccess() {
        MoveDTO moveDTO = MoveDTO.builder()
                .playerKey(gameMock.getFirstPlayerKey().toString())
                .position(3)
                .build();

        PlayerDTO playerMock = PlayerDTO.builder()
                .playerNumber(1)
                .playerMancala(gameMock.getFirstPlayerMancala())
                .playerTurn(true)
                .playerName(gameMock.getFirstPlayerName())
                .playerKey(gameMock.getFirstPlayerKey().toString())
                .cups(gameMock.getCups())
                .status(gameMock.getStatus())
                .build();

        when(gameService.getPlayer(gameMock.getFirstPlayerKey().toString()))
                .thenReturn(playerMock);

        when(gameService.getGame(gameMock.getFirstPlayerKey().toString()))
                .thenReturn(gameMock);

        PlayerDTO move = roundService.move(moveDTO);

        Assertions.assertEquals(1, move.getPlayerMancala());
    }

    @Test
    void moveOtherPlayerCup() {
        MoveDTO moveDTO = MoveDTO.builder()
                .playerKey(gameMock.getFirstPlayerKey().toString())
                .position(3)
                .build();

        PlayerDTO playerMock = PlayerDTO.builder()
                .playerNumber(2)
                .playerMancala(gameMock.getFirstPlayerMancala())
                .playerTurn(true)
                .playerName(gameMock.getFirstPlayerName())
                .playerKey(gameMock.getFirstPlayerKey().toString())
                .cups(gameMock.getCups())
                .status(gameMock.getStatus())
                .build();

        when(gameService.getPlayer(gameMock.getFirstPlayerKey().toString()))
                .thenReturn(playerMock);

        when(gameService.getGame(gameMock.getFirstPlayerKey().toString()))
                .thenReturn(gameMock);

        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class, () -> {
            roundService.move(moveDTO);
        });

        System.out.println(responseStatusException);
    }

    @Test
    void moveSecondPlayerSuccess() {
        MoveDTO moveDTO = MoveDTO.builder()
                .playerKey(gameMock.getFirstPlayerKey().toString())
                .position(9)
                .build();

        PlayerDTO playerMock = PlayerDTO.builder()
                .playerNumber(2)
                .playerMancala(gameMock.getFirstPlayerMancala())
                .playerTurn(true)
                .playerName(gameMock.getFirstPlayerName())
                .playerKey(gameMock.getFirstPlayerKey().toString())
                .cups(gameMock.getCups())
                .status(gameMock.getStatus())
                .build();

        when(gameService.getPlayer(gameMock.getFirstPlayerKey().toString()))
                .thenReturn(playerMock);

        when(gameService.getGame(gameMock.getFirstPlayerKey().toString()))
                .thenReturn(gameMock);

        PlayerDTO move = roundService.move(moveDTO);

        Assertions.assertEquals(1, move.getPlayerMancala());
    }

    private Map<Integer, Integer> createBoard() {
        Map<Integer, Integer> map = new HashMap<>();

        for(int i = 1; i <= 12; i++) {
            map.put(i, 4);
        }

        return map;
    }
}