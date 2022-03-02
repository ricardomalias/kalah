package com.game.kalah.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {

    @PostMapping
    public String createGame() {
        return "geroudo";
    }

    @GetMapping(value = "/game/{gameId}")
    public String getGame(@PathVariable String gameId) {
        return "geroudo";
    }
}
