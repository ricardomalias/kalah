package com.game.kalah.service;

import com.game.kalah.dto.GameDTO;
import com.game.kalah.dto.PlayerDTO;
import com.game.kalah.model.Game;
import com.game.kalah.model.GameStatus;
import com.game.kalah.model.PlayerPing;
import com.game.kalah.repository.GameRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class GameService {

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public Game createGame(GameDTO gameDTO) {
        Game build = Game.builder()
                .firstPlayerName(gameDTO.getFirstPlayerName())
                .secondPlayerName(gameDTO.getSecondPlayerName())
                .firstPlayerKey(UUID.randomUUID())
                .secondPlayerKey(UUID.randomUUID())
                .status(GameStatus.WAITING)
                .matchTime(0L)
                .build();

        return gameRepository.save(build);
    }

    private Game getGame(String playerKey) {
        Optional<Game> byFirstPlayerKey = gameRepository.findByFirstPlayerKey(UUID.fromString(playerKey));

        if(byFirstPlayerKey.isPresent()) {
            return byFirstPlayerKey.get();
        }

        Optional<Game> bySecondPlayerKey = gameRepository.findBySecondPlayerKey(UUID.fromString(playerKey));

        if(bySecondPlayerKey.isPresent()) {
            return bySecondPlayerKey.get();
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "player not found");
    }

    public PlayerDTO getPlayer(String playerKey) {
        Game game = getGame(playerKey);

        String playerName = "";
        PlayerPing playerPing = null;
        if(playerKey.equals(game.getFirstPlayerKey().toString())) {
            playerName = game.getFirstPlayerName();
            playerPing = game.getFirstPlayerPing();
        }

        if(playerKey.equals(game.getSecondPlayerKey().toString())) {
            playerName = game.getSecondPlayerName();
            playerPing = game.getSecondPlayerPing();
        }

        return PlayerDTO.builder()
                .playerKey(playerKey)
                .playerName(playerName)
                .playerPing(playerPing)
                .matchTime(game.getMatchTime())
                .status(game.getStatus())
                .build();
    }

    public void pingGame(String playerKey) {
        Game game = getGame(playerKey);

        if(playerKey.equals(game.getFirstPlayerKey().toString())) {
            game.setFirstPlayerPing(LocalDateTime.now());
        }

        if(playerKey.equals(game.getSecondPlayerKey().toString())) {
            game.setSecondPlayerPing(LocalDateTime.now());
        }

        gameRepository.save(game);
    }
}
