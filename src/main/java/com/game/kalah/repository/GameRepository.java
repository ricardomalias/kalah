package com.game.kalah.repository;

import com.game.kalah.model.Game;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GameRepository extends MongoRepository<Game, String> {
    Optional<Game> findByFirstPlayerKey(UUID gameKey);
    Optional<Game> findBySecondPlayerKey(UUID gameKey);
    Optional<Game> findByFirstPlayerKeyOrSecondPlayerKey(UUID gameKey);
}
