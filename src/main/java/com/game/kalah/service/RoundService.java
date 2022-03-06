package com.game.kalah.service;

import com.game.kalah.dto.MoveDTO;
import com.game.kalah.dto.PlayerDTO;
import com.game.kalah.dto.RoundDTO;
import com.game.kalah.model.Game;
import com.game.kalah.model.GameStatus;
import com.game.kalah.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class RoundService {

    public RoundService(GameService gameService, MessageUtil messageUtil) {
        this.gameService = gameService;
        this.messageUtil = messageUtil;
    }

    private final MessageUtil messageUtil;

    private final GameService gameService;

    public PlayerDTO move(MoveDTO moveDTO) {
        PlayerDTO player = gameService.getPlayer(moveDTO.getPlayerKey());

        if(!player.isPlayerTurn()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, messageUtil.getMessage("round.not_turn"), null);
        }

        if(!checkPlayerMove(moveDTO, player)) {
            System.out.println(messageUtil.getMessage("round.forbidden_cup"));
            throw new ResponseStatusException(HttpStatus.CONFLICT, messageUtil.getMessage("round.forbidden_cup"), null);
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
            gameBuilder.secondPlayerMancala(game.getSecondPlayerMancala());
        }

        if(player.getPlayerNumber() == 2) {
            gameBuilder.secondPlayerMancala(player.getPlayerMancala());
            gameBuilder.firstPlayerMancala(game.getFirstPlayerMancala());
        }

        return gameBuilder.build();
    }

    private RoundDTO calculateMove(MoveDTO moveDTO, PlayerDTO player) {
        Map<Integer, Integer> cups = player.getCups();
        int cupStones = cups.get(moveDTO.getPosition());
        cups.put(moveDTO.getPosition(), 0);

        ArrayList<Integer> listStones = createListStones(cupStones);
        Iterator<Integer> iterator = listStones.iterator();

        mancalaPoint(player, moveDTO.getPosition(), iterator);
        int offset = offsetCalculator(moveDTO.getPosition(), player);
        int nextTurn = player.getPlayerNumber() == 1 ? 2 : 1;

        while(iterator.hasNext()) {
            log.info("offset is: {} cupStones: {}", offset, cupStones);

            iterator.next();

            if(!iterator.hasNext() && cups.get(offset) == 0) {
                Optional<Integer> intersect = getIntersect(offset, player);
                log.info("intersect: {}", intersect);
                if(intersect.isPresent()) {
                    int points = cups.get(intersect.get()) + 1;
                    player.addPoint(points);

                    cups.put(offset, 0);
                    cups.put(intersect.get(), 0);
                }
            } else {
                int cup = cups.get(offset);
                cups.put(offset, cup + 1);
            }

            boolean scored = mancalaPoint(player, offset, iterator);

            if(scored && !iterator.hasNext()) {
                nextTurn = player.getPlayerNumber();
                break;
            }

            offset = offsetCalculator(offset, player);
        }

        return RoundDTO.builder()
                .player(player)
                .playerTurnNumber(nextTurn)
                .cups(cups)
                .build();
    }

    private int offsetCalculator(int offset, PlayerDTO player) {
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

        if((player.getPlayerNumber() == 1 && offset == 6) || (player.getPlayerNumber() == 2 && offset == 12)) {
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

    private boolean checkPlayerMove(MoveDTO moveDTO, PlayerDTO player) {
        return player.getPlayerNumber() == 1 && moveDTO.getPosition() < 7
                || player.getPlayerNumber() == 2 && moveDTO.getPosition() > 6;
    }

    private Optional<Integer> getIntersect(int offset, PlayerDTO player) {
        Map<Integer, Integer> correlation = new HashMap<>();
        int size = player.getCups().size();

        for(int i = 0; i < size/2; i++) {
            if(player.getPlayerNumber() == 1) {
                correlation.put(i+1, size-i);
            }

            if(player.getPlayerNumber() == 2) {
                correlation.put(size-i, i+1);
            }
        }

        if(correlation.containsKey(offset)) {
            return Optional.of(correlation.get(offset));
        }

        return Optional.empty();
    }
}
