package com.game.kalah.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
public class GameDTO {
    @NotBlank(message = "game.first_player_not_found")
    private String firstPlayerName;

    @NotBlank(message = "game.second_player_not_found")
    private String secondPlayerName;
}
