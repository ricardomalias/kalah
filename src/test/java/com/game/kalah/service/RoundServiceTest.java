package com.game.kalah.service;

import com.game.kalah.dto.MoveDTO;
import com.game.kalah.dto.PlayerDTO;
import com.game.kalah.model.Game;
import com.game.kalah.model.GameStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class RoundServiceTest {

    private GameService gameService;
    private RoundService roundService;

    private Game gameMock;

    @BeforeEach
    void setUp() {
        gameService = Mockito.mock(GameService.class);
        roundService = new RoundService(gameService);

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
        int positionToMove = new Random()
                .ints(1, 6)
                .findFirst()
                .orElse(3);

        MoveDTO moveDTO = MoveDTO.builder()
                .playerKey(gameMock.getFirstPlayerKey().toString())
                .position(positionToMove)
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
        Mockito.verify(gameService, Mockito.times(1)).updateGame(any());

        Assertions.assertEquals(0, move.getCups().get(positionToMove));
    }

    @Test
    void moveSecondPlayerSuccess() {
        int positionToMove = new Random()
                .ints(7, 12)
                .findFirst()
                .orElse(8);

        MoveDTO moveDTO = MoveDTO.builder()
                .playerKey(gameMock.getFirstPlayerKey().toString())
                .position(positionToMove)
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
        Mockito.verify(gameService, Mockito.times(1)).updateGame(any());

        Assertions.assertEquals(0, move.getCups().get(positionToMove));
    }

    @Test
    void moveOtherPlayerCupError() {
        int positionToMove = new Random()
                .ints(1, 12)
                .findFirst()
                .orElse(8);

        MoveDTO moveDTO = MoveDTO.builder()
                .playerKey(gameMock.getFirstPlayerKey().toString())
                .position(positionToMove)
                .build();

        PlayerDTO playerMock = PlayerDTO.builder()
                .playerNumber(positionToMove > 6 ? 1 : 2)
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

        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class, () -> roundService.move(moveDTO));

        Mockito.verify(gameService, Mockito.times(0)).updateGame(any());

        Assertions.assertTrue(Objects.requireNonNull(responseStatusException.getMessage()).contains("round.forbidden_cup"));
    }

    @Test
    void calculateCapturePieces() {
        MoveDTO moveDTO = MoveDTO.builder()
                .playerKey(gameMock.getFirstPlayerKey().toString())
                .position(1)
                .build();

        Map<Integer, Integer> cups = gameMock.getCups();
        cups.put(5,0);

        PlayerDTO playerMock = PlayerDTO.builder()
                .playerNumber(1)
                .playerMancala(gameMock.getFirstPlayerMancala())
                .playerTurn(true)
                .playerName(gameMock.getFirstPlayerName())
                .playerKey(gameMock.getFirstPlayerKey().toString())
                .cups(cups)
                .status(gameMock.getStatus())
                .build();

        when(gameService.getPlayer(gameMock.getFirstPlayerKey().toString()))
                .thenReturn(playerMock);

        when(gameService.getGame(gameMock.getFirstPlayerKey().toString()))
                .thenReturn(gameMock);

        PlayerDTO move = roundService.move(moveDTO);

        Assertions.assertEquals(5, move.getPlayerMancala());
    }

    @Test
    void checkTurnRepetition() {
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

        Assertions.assertTrue(move.isPlayerTurn());
    }

    private Map<Integer, Integer> createBoard() {
        Map<Integer, Integer> map = new HashMap<>();

        for(int i = 1; i <= 12; i++) {
            map.put(i, 4);
        }

        return map;
    }

    @Test
    void roundEndGame() {
        Map<Integer, Integer> cups = createBoard();
        cups.put(1, 0);
        cups.put(2, 0);
        cups.put(3, 0);
        cups.put(4, 0);
        cups.put(5, 0);
        cups.put(6, 0);

        Game game = Game.builder()
                .idGame("123")
                .firstPlayerName("Asdrubal")
                .secondPlayerName("Astolfo")
                .firstPlayerKey(UUID.randomUUID())
                .secondPlayerKey(UUID.randomUUID())
                .status(GameStatus.WAITING)
                .matchTime(0L)
                .cups(cups)
                .build();

        MoveDTO moveDTO = MoveDTO.builder()
                .playerKey(game.getFirstPlayerKey().toString())
                .position(3)
                .build();

        PlayerDTO playerMock = PlayerDTO.builder()
                .playerNumber(1)
                .playerMancala(game.getFirstPlayerMancala())
                .playerTurn(true)
                .playerName(game.getFirstPlayerName())
                .playerKey(game.getFirstPlayerKey().toString())
                .cups(game.getCups())
                .status(game.getStatus())
                .build();

        PlayerDTO playerMock2 = PlayerDTO.builder()
                .playerNumber(1)
                .playerMancala(game.getFirstPlayerMancala())
                .playerTurn(true)
                .playerName(game.getFirstPlayerName())
                .playerKey(game.getFirstPlayerKey().toString())
                .cups(game.getCups())
                .status(GameStatus.FINISHED)
                .build();

        when(gameService.getPlayer(game.getFirstPlayerKey().toString()))
                .thenReturn(playerMock)
                .thenReturn(playerMock2);

        when(gameService.getGame(game.getFirstPlayerKey().toString()))
                .thenReturn(game);

        PlayerDTO move = roundService.move(moveDTO);

        assertEquals(GameStatus.FINISHED, move.getStatus());
    }
}