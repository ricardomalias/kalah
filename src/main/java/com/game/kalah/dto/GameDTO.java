package com.game.kalah.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GameDTO {
    private String firstPlayerName;
    private String secondPlayerName;
}
