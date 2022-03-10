package com.game.kalah.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Builder
@Getter
@Document
public class Game {

    @Id
    @JsonIgnore
    private String idGame;

    private UUID firstPlayerKey;
    private UUID secondPlayerKey;

    private String firstPlayerName;
    private String secondPlayerName;

    private PlayerPing firstPlayerPing;

    public void setFirstPlayerPing(LocalDateTime ping) {
        this.firstPlayerPing = new PlayerPing(ping);
    }

    private PlayerPing secondPlayerPing;

    public void setSecondPlayerPing(LocalDateTime ping) {
        this.secondPlayerPing = new PlayerPing(ping);
    }

    private int firstPlayerMancala;
    private int secondPlayerMancala;

    private int playerTurnNumber;
    private int matchTurn;
    private long matchTime;

    public void setMatchTime(long matchTime) {
        this.matchTime = matchTime;
    }

    private LocalDateTime createDate;
    private GameStatus status;

    public void setGameStatus(GameStatus gameStatus) {
        this.status = gameStatus;
    }

    private Map<Integer, Integer> cups;
}
