package com.game.kalah.service;

import com.game.kalah.dto.GameDTO;
import com.game.kalah.dto.PlayerDTO;
import com.game.kalah.model.Game;
import com.game.kalah.model.GameStatus;
import com.game.kalah.model.PlayerPing;
import com.game.kalah.repository.GameRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class GameService {

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    private Map<Integer, Integer> createBoard() {
        Map<Integer, Integer> map = new HashMap<>();

        for(int i = 1; i <= 12; i++) {
            map.put(i, 4);
        }

        return map;
    }

    public Game createGame(GameDTO gameDTO) {
        Game build = Game.builder()
                .firstPlayerName(gameDTO.getFirstPlayerName())
                .secondPlayerName(gameDTO.getSecondPlayerName())
                .firstPlayerKey(UUID.randomUUID())
                .secondPlayerKey(UUID.randomUUID())
                .status(GameStatus.WAITING)
                .matchTime(0L)
                .createDate(LocalDateTime.now())
                .cups(createBoard())
                .build();

        return gameRepository.save(build);
    }

    public Game getGame(String playerKey) {
        Optional<Game> byFirstPlayerKey = gameRepository.findByFirstPlayerKey(UUID.fromString(playerKey));

        if(byFirstPlayerKey.isPresent()) {
            return byFirstPlayerKey.get();
        }

        Optional<Game> bySecondPlayerKey = gameRepository.findBySecondPlayerKey(UUID.fromString(playerKey));

        if(bySecondPlayerKey.isPresent()) {
            return bySecondPlayerKey.get();
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "game.player_not_found");
    }

    public PlayerDTO getPlayer(String playerKey) {
        Game game = getGame(playerKey);

        int playerNumber = playerKey.equals(game.getFirstPlayerKey().toString()) ? 1 : 2;
        boolean playerTurn = game.getPlayerTurnNumber() == playerNumber || game.getPlayerTurnNumber() == 0 && playerNumber == 1;
        String playerName = "";
        PlayerPing playerPing = null;
        int playerMancala = 0;
        int opponentMancala = 0;

        if(playerNumber == 1) {
            playerName = game.getFirstPlayerName();
            playerPing = game.getFirstPlayerPing();
            playerMancala = game.getFirstPlayerMancala();
            opponentMancala = game.getSecondPlayerMancala();
        }

        if(playerNumber == 2) {
            playerName = game.getSecondPlayerName();
            playerPing = game.getSecondPlayerPing();
            playerMancala = game.getSecondPlayerMancala();
            opponentMancala = game.getFirstPlayerMancala();
        }

        return PlayerDTO.builder()
                .playerNumber(playerNumber)
                .playerKey(playerKey)
                .playerName(playerName)
                .playerPing(playerPing)
                .playerTurn(playerTurn)
                .playerMancala(playerMancala)
                .opponentMancala(opponentMancala)
                .matchTime(game.getMatchTime())
                .matchTurn(game.getMatchTurn())
                .createDate(game.getCreateDate())
                .status(game.getStatus())
                .cups(game.getCups())
                .build();
    }

    public void pingGame(String playerKey) {
        Game game = getGame(playerKey);

        if(game.getStatus() == GameStatus.FINISHED) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "game.status_already_finished");
        }

        LocalDateTime now = LocalDateTime.now();
        Duration between = Duration.between(now, game.getCreateDate());
        long timestamp = Math.abs(between.toMillis());
        game.setMatchTime(timestamp);

        if(playerKey.equals(game.getFirstPlayerKey().toString())) {
            game.setFirstPlayerPing(now);
        }

        if(playerKey.equals(game.getSecondPlayerKey().toString())) {
            game.setSecondPlayerPing(now);
        }

        if(game.getFirstPlayerPing() != null && game.getSecondPlayerPing() != null) {
            game.setGameStatus(GameStatus.RUNNING);
        }

        gameRepository.save(game);
    }

    public void updateGame(Game game) {
        gameRepository.save(game);
    }

    public void endGame(Game game) {
        Map<Integer, Integer> cups = game.getCups();
        int firstPlayerMancala = game.getFirstPlayerMancala();
        int secondPlayerMancala = game.getSecondPlayerMancala();

        for(int i = 1; i <= cups.size(); i++) {
            if(i < 7) {
                firstPlayerMancala = firstPlayerMancala + cups.get(i);
                cups.put(i, 0);
            } else {
                secondPlayerMancala = secondPlayerMancala + cups.get(i);
                cups.put(i, 0);
            }
        }

        Game gameBuilder = Game.builder()
                .idGame(game.getIdGame())
                .firstPlayerName(game.getFirstPlayerName())
                .secondPlayerName(game.getSecondPlayerName())
                .firstPlayerKey(game.getFirstPlayerKey())
                .secondPlayerKey(game.getSecondPlayerKey())
                .playerTurnNumber(game.getPlayerTurnNumber())
                .firstPlayerMancala(firstPlayerMancala)
                .secondPlayerMancala(secondPlayerMancala)
                .matchTurn(game.getMatchTurn())
                .status(GameStatus.FINISHED)
                .cups(cups)
                .build();

        gameRepository.save(gameBuilder);
    }
}
