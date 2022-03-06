package com.game.kalah.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter
@Builder
public class MoveDTO {

    @NotBlank(message = "round.player_key_not_found")
    private String playerKey;

    @Min(value = 1, message = "round.min_position")
    @Max(value = 12, message = "round.max_position")
    private int position;
}
