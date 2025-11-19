package com.sportsbet.codingchallenge.depthchart.api.repository;

import com.sportsbet.codingchallenge.depthchart.api.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    Optional<Player> findByUniqueTeamNumber(Integer uniqueTeamNumber);

    Optional<Player> findByPlayerNameAndUniqueTeamNumber(String playerName,Integer uniqueTeamNumber);
}
