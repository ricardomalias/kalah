package com.game.kalah.service;

import com.game.kalah.dto.MoveDTO;
import com.game.kalah.dto.PlayerDTO;
import com.game.kalah.dto.RoundDTO;
import com.game.kalah.model.Game;
import com.game.kalah.model.GameStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Iterator;
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

        if(!player.isPlayerTurn()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "not your turn");
        }

        if(!checkPlayerMove(moveDTO, player)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "you cannot move others player cup");
        }

        RoundDTO roundDTO = calculateMove(moveDTO, player);
        player.endTurn(roundDTO.getPlayerTurnNumber());

        Game gameBuilder = roundToGame(roundDTO);
        gameService.updateGame(gameBuilder);

        return roundDTO.getPlayer();
    }

    private Game roundToGame(RoundDTO roundDTO) {
        PlayerDTO player = roundDTO.getPlayer();
        Game game = gameService.getGame(player.getPlayerKey());

        Game.GameBuilder gameBuilder = Game.builder()
                .idGame(game.getIdGame())
                .firstPlayerName(game.getFirstPlayerName())
                .secondPlayerName(game.getSecondPlayerName())
                .firstPlayerKey(game.getFirstPlayerKey())
                .secondPlayerKey(game.getSecondPlayerKey())
                .playerTurnNumber(roundDTO.getPlayerTurnNumber())
                .matchTurn(player.getMatchTurn())
                .status(GameStatus.RUNNING)
                .cups(roundDTO.getCups());

        if(player.getPlayerNumber() == 1) {
            gameBuilder.firstPlayerMancala(player.getPlayerMancala());
        }

        if(player.getPlayerNumber() == 2) {
            gameBuilder.secondPlayerMancala(player.getPlayerMancala());
        }

        return gameBuilder.build();
    }

    private RoundDTO calculateMove(MoveDTO moveDTO, PlayerDTO player) {
        Map<Integer, Integer> cups = player.getCups();
        int cupStones = cups.get(moveDTO.getPosition());
        cups.put(moveDTO.getPosition(), 0);

        ArrayList<Integer> listStones = createListStones(cupStones);
        Iterator<Integer> iterator = listStones.iterator();

        int offset = offsetCalculator(moveDTO.getPosition(), player, iterator);
        int nextTurn = player.getPlayerNumber() == 1 ? 2 : 1;

        while(iterator.hasNext()) {
            log.info("offset is: {} cupStones: {}", offset, cupStones);

            int cup = cups.get(offset);
            cups.put(offset, cup + 1);
            iterator.next();

            boolean scored = mancalaPoint(player, offset, iterator);

            if(scored && !iterator.hasNext()) {
                nextTurn = player.getPlayerNumber();
                break;
            }

            offset = offsetCalculator(offset, player, iterator);
        }

        return RoundDTO.builder()
                .player(player)
                .playerTurnNumber(nextTurn)
                .cups(cups)
                .build();
    }

    private int offsetCalculator(int offset, PlayerDTO player, Iterator<Integer> iterator) {
        mancalaPoint(player, offset, iterator);
        offset++;
        if(offset > player.getCups().size()) {
            offset = 1;
        }

        return offset;
    }

    private boolean mancalaPoint(PlayerDTO player, int offset, Iterator<Integer> iterator) {
        if(!iterator.hasNext()) {
            return false;
        }

        if(player.getPlayerNumber() == 1 && offset == 6 || player.getPlayerNumber() == 2 && offset == 12) {
            player.addPoint(1);
            iterator.next();
            return true;
        }

        return false;
    }

    private ArrayList<Integer> createListStones(int cupStones) {
        ArrayList<Integer> stones = new ArrayList<>();
        for(int i = cupStones; i > 0; i--) {
            stones.add(1);
        }

        return stones;
    }

    private int findIntersect(int offset, int total) {
        int halfSize = total / 2;
        return 7 - (offset % (total / 2));
    }

    private boolean checkPlayerMove(MoveDTO moveDTO, PlayerDTO player) {
        return player.getPlayerNumber() == 1 && moveDTO.getPosition() < 7
                || player.getPlayerNumber() == 2 && moveDTO.getPosition() > 6;
    }
}
