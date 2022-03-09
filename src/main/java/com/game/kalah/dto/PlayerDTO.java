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
    private int opponentMancala;
    private long matchTime;
    private int matchTurn;
    private GameStatus status;
    private Map<Integer, Integer> cups;

    public void addPoint(int point) {
        this.playerMancala = this.playerMancala + point;
    }

    public void endTurn(int playerTurnNumber) {
        this.matchTurn = this.getMatchTurn() + 1;
        this.playerTurn = this.getPlayerNumber() == playerTurnNumber;
    }
}
