package com.game.kalah.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MoveDTO {

    private String playerKey;
    private int position;
}
