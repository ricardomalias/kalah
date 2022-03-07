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

import java.sql.Timestamp;
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

        if(playerNumber == 1) {
            playerName = game.getFirstPlayerName();
            playerPing = game.getFirstPlayerPing();
            playerMancala = game.getFirstPlayerMancala();
        }

        if(playerNumber == 2) {
            playerName = game.getSecondPlayerName();
            playerPing = game.getSecondPlayerPing();
            playerMancala = game.getSecondPlayerMancala();
        }

        return PlayerDTO.builder()
                .playerNumber(playerNumber)
                .playerKey(playerKey)
                .playerName(playerName)
                .playerPing(playerPing)
                .playerTurn(playerTurn)
                .playerMancala(playerMancala)
                .matchTime(game.getMatchTime())
                .matchTurn(game.getMatchTurn())
                .status(game.getStatus())
                .cups(game.getCups())
                .build();
    }

    public void pingGame(String playerKey) {
        Game game = getGame(playerKey);
        LocalDateTime now = LocalDateTime.now();
        game.setMatchTime(Timestamp.valueOf(now).getTime() - game.getMatchTime());

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
}
