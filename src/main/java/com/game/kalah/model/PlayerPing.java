package com.game.kalah.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PlayerPing {

    private LocalDateTime pingTime;
}
