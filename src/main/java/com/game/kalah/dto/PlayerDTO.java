package com.game.kalah.dto;

import com.game.kalah.model.GameStatus;
import com.game.kalah.model.PlayerPing;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Builder
@Getter
public class PlayerDTO {

    private int playerNumber;
    private String playerName;
    private String playerKey;
    private PlayerPing playerPing;
    private boolean playerTurn;
    private int playerMancala;
    private long matchTime;
    private int matchTurn;
    private GameStatus status;
    private Map<Integer, Integer> cups;

    public void setCups(Map<Integer, Integer> cups) {
        this.cups = cups;
    }

    public void addPoint(int point) {
        this.playerMancala = this.playerMancala + point;
    }
}
