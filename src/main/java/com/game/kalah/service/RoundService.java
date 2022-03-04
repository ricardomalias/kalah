package com.game.kalah.service;

import com.game.kalah.dto.MoveDTO;
import com.game.kalah.dto.PlayerDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class RoundService {

    public RoundService(GameService gameService) {
        this.gameService = gameService;
    }

    private final GameService gameService;

    public PlayerDTO move(MoveDTO moveDTO) {
        PlayerDTO player = gameService.getPlayer(moveDTO.getPlayerKey());

//        if(!player.isPlayerTurn()) {
//            throw new ResponseStatusException(HttpStatus.CONFLICT, "not your turn");
//        }

//        if(!checkPlayerMove(moveDTO, player)) {
//            throw new ResponseStatusException(HttpStatus.CONFLICT, "you cannot move others player cup");
//        }

        Map<Integer, Integer> cups = player.getCups();
        int cupStone = cups.get(moveDTO.getPosition());
        int offset = moveDTO.getPosition();

        for(int i = 1; i <= cupStone; i++) {
            offset++;

            int cup = cups.get(offset);
            log.info("cup amount {}", cup);
            cups.put(offset, cup + 1);

            if(offset % 6 == 0) {
                offset++;
                player.addPoint(1);
            }

            if(offset > cups.size()) {
                offset = 1;
            }

            log.info("offset is: {} size is: {} equals: {} ", offset, player.getCups().size(), offset == player.getCups().size());
        }

        player.setCups(cups);

        log.info("will move until: {} ", offset);

        return player;
    }

    private boolean checkPlayerMove(MoveDTO moveDTO, PlayerDTO player) {
        return player.getPlayerNumber() == 1 && moveDTO.getPosition() < 7
                || player.getPlayerNumber() == 2 && moveDTO.getPosition() > 6;
    }
}
