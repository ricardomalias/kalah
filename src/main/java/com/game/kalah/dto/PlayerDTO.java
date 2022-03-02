package com.game.kalah.dto;

import com.game.kalah.model.GameStatus;
import com.game.kalah.model.PlayerPing;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PlayerDTO {

    private String playerName;
    private String playerKey;
    private PlayerPing playerPing;
    private long matchTime;
    private GameStatus status;
}
