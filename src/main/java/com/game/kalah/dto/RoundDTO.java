package com.game.kalah.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class RoundDTO {

    private int playerTurnNumber;
    private PlayerDTO player;
    private Map<Integer, Integer> cups;
}
