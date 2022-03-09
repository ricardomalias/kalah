package com.game.kalah.controller;

import com.game.kalah.dto.GameDTO;
import com.game.kalah.dto.PlayerDTO;
import com.game.kalah.model.Game;
import com.game.kalah.service.GameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("game")
@Slf4j
public class GameController {

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    private final GameService gameService;

    @PostMapping(value = "/")
    public ResponseEntity<Game> createGame(@RequestBody GameDTO gameDTO) {
        Game game = gameService.createGame(gameDTO);
        return ResponseEntity.ok(game);
    }

    @GetMapping(value = "/{playerKey}/player")
    public ResponseEntity<PlayerDTO> getPlayer(@PathVariable String playerKey) {
        PlayerDTO player = gameService.getPlayer(playerKey);

        return ResponseEntity.ok(player);
    }

    @PatchMapping(value = "/{playerKey}/ping")
    @Async
    public ResponseEntity<String> pingGame(@PathVariable String playerKey) {
        gameService.pingGame(playerKey);

        return ResponseEntity.accepted().body("");
    }
}
