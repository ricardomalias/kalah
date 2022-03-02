package com.game.kalah.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum GameStatus {
    WAITING,
    RUNNING,
    FINISHED
}
