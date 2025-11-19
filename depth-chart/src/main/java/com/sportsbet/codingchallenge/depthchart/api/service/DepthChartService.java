package com.sportsbet.codingchallenge.depthchart.api.service;

import com.sportsbet.codingchallenge.depthchart.api.DepthChartDTO;
import com.sportsbet.codingchallenge.depthchart.api.PlayerDTO;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service for managing a depth charts of players for a given position
 */
public interface DepthChartService {

    DepthChartDTO createDepthChartData(DepthChartDTO depthChartDTO);

    List<PlayerDTO> retrieveBackupsForAPLayerOnAPosition(String position, String playerName, Integer playerUniqueNumber);

    Map<String,List<PlayerDTO>> retrieveFullDepthChart();

    Optional<PlayerDTO> deletePlayerOnAPosition(String position, String playerName, Integer playerUniqueNumber);
}
