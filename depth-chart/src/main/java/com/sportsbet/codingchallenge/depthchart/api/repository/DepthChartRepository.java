package com.sportsbet.codingchallenge.depthchart.api.repository;

import com.sportsbet.codingchallenge.depthchart.api.DepthChart;
import com.sportsbet.codingchallenge.depthchart.api.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepthChartRepository extends JpaRepository<DepthChart, Long> {

    List<DepthChart> findByPosition(String position);

    Optional<DepthChart> findByPlayerAndPosition(Player player, String position);
}
