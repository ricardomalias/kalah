package com.game.kalah.controller;

import com.game.kalah.dto.MoveDTO;
import com.game.kalah.dto.PlayerDTO;
import com.game.kalah.service.RoundService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("round")
public class RoundController {

    public RoundController(RoundService roundService) {
        this.roundService = roundService;
    }

    private final RoundService roundService;

    @PutMapping(value = "/move")
    public ResponseEntity<PlayerDTO> getGame(@RequestBody @Valid MoveDTO moveDTO) {
        PlayerDTO playerDTO = roundService.move(moveDTO);

        return ResponseEntity.ok(playerDTO);
    }

}
