package com.sportsbet.codingchallenge.depthchart.api.service;

import com.sportsbet.codingchallenge.depthchart.api.DepthChart;
import com.sportsbet.codingchallenge.depthchart.api.DepthChartDTO;
import com.sportsbet.codingchallenge.depthchart.api.Player;
import com.sportsbet.codingchallenge.depthchart.api.PlayerDTO;
import com.sportsbet.codingchallenge.depthchart.api.exception.DepthChartCreationException;
import com.sportsbet.codingchallenge.depthchart.api.exception.PlayerNotFoundException;
import com.sportsbet.codingchallenge.depthchart.api.exception.PositionNotFoundException;
import com.sportsbet.codingchallenge.depthchart.api.repository.DepthChartRepository;
import com.sportsbet.codingchallenge.depthchart.api.repository.PlayerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of service for managing a depth charts of players for a given position
 */

@Service
@Slf4j
@AllArgsConstructor
public class DepthChartServiceImpl implements DepthChartService {

    private DepthChartRepository depthChartRepository;
    private PlayerRepository playerRepository;

    /**
     * create a depth chart for a given position for a given player
     * @param depthChartDTO
     * @return DepthChartDTO
     */
    public DepthChartDTO createDepthChartData(DepthChartDTO depthChartDTO) {
        log.debug("Creating depth chart data for position : {}", depthChartDTO.position());

        DepthChart depthChart = new DepthChart();

        //Assumption 1 -> If the player is not found in the database the exception is thrown
        Player player = retrievePlayer(depthChartDTO.player().playerName(), depthChartDTO.player().uniqueTeamNumber());

        //Assuming that already added player for a position
        // is requested  to be added for the same position again - should not be created and an exception is thrown
        List<DepthChart> depthChartList = depthChartRepository.findByPosition(depthChartDTO.position());

       //check if the player is already added to the given position
        DepthChart depthChart2 = depthChartList.stream()
                        .filter(
                                dc->(
                                        dc.getPlayer().getUniqueTeamNumber()
                                        .equals(player.getUniqueTeamNumber())
                                ))
                .findFirst().orElse(null);
        if(depthChart2 != null) {
            log.error("Same Player : {} for same position : {} has already been created in the depth chart "
                    , player.getPlayerName(), depthChart2.getPosition());
           throw new DepthChartCreationException("Player : " + player.getPlayerName() + " has already been created in the depth chart");
        }

        /*
          When the position depth is missing in the request put the player at the end
         */
        Integer positionDepth;
        if(depthChartDTO.positionDepth() == null){
            log.info("Position Depth is missing in the input at position: {}", depthChartDTO.position());
            //get the max or 1
            positionDepth = depthChartList.stream()
                        .mapToInt(DepthChart::getPositionDepth)
                        .max().orElse(-1)+1;

        } else {
            positionDepth = depthChartDTO.positionDepth();
        }

        //as per the requirement all the players move down after the given position_depth
        List<DepthChart> depthChartList1 = depthChartList.stream()
                .filter(dcd -> dcd.getPositionDepth() >= positionDepth)//move down only if the player is inserted in the between the position depth
                .peek(dcd->dcd.setPositionDepth(dcd.getPositionDepth()+1))
                .toList();

        depthChart.setPlayer(player);
        depthChart.setPositionDepth(positionDepth);
        depthChart.setPosition(depthChartDTO.position());
        var savedDepthChart = depthChartRepository.save(depthChart);

        if(!depthChartList1.isEmpty()){
            depthChartRepository.saveAll(depthChartList1);
        }

        return mapToDepthChartDTO(savedDepthChart);
    }

    /**
     * To retrieve a backup for a player - all the player below depth for a given position
     * @param position
     * @param playerName
     * @param playerUniqueNumber
     * @return List<PlayerDTO>
     */
    public List<PlayerDTO> retrieveBackupsForAPLayerOnAPosition(String position, String playerName, Integer playerUniqueNumber) {
        log.debug("Retrieving backups for position : {} for player : {}", position, playerName);
        //check if player exists
        Player player = retrievePlayer(playerName,playerUniqueNumber);

        List<DepthChart> depthChartList = retrieveDepthChartForPosition(position);

        //find the position depth of the given player for the given position
        Optional<Integer> playerPositionDepth = depthChartList.stream()
                .filter(depthChart -> depthChart.getPlayer().getUniqueTeamNumber().equals( playerUniqueNumber))
                .findFirst()
                .map(DepthChart::getPositionDepth);


        return playerPositionDepth.map(integer -> depthChartList.stream()
                .filter(depthChart ->
                        depthChart.getPositionDepth() > integer)
                .sorted(Comparator.comparingInt(DepthChart::getPositionDepth))
                .map(this::mapToPLayerDTO)
                .toList()).orElse(Collections.emptyList());

    }

    /**
     * map a depth chart to a playerDTO
     * @param depthChart
     * @return playerDTO
     */
    private PlayerDTO mapToPLayerDTO(DepthChart depthChart) {

        return new PlayerDTO(depthChart.getPlayer().getId(),depthChart.getPlayer().getPlayerName(),
                depthChart.getPlayer().getUniqueTeamNumber());

    }

    /**
     * map a Depth chart entity to corresponding Depth Chart DTO
     * @param depthChart
     * @return DepthChart DTO
     */
    private DepthChartDTO mapToDepthChartDTO(DepthChart depthChart) {

        return new DepthChartDTO(depthChart.getDepthChartDataId(),
                                 depthChart.getPosition(),
                                 depthChart.getPositionDepth(),
                                 mapToPLayerDTO(depthChart));


    }

    /**
     * To retrieve the full depth chart
     * @return the map of postion as key and the list of the players added as value
     */
    public Map<String,List<PlayerDTO>> retrieveFullDepthChart() {

        log.info("Retrieving full depth chart data");

        List<DepthChart> depthChartList = depthChartRepository.findAll();
        return depthChartList.stream()
                .sorted(Comparator.comparingInt(DepthChart::getPositionDepth))
                .collect(Collectors.groupingBy(
                        DepthChart::getPosition,
                        Collectors.mapping(
                                this::mapToPLayerDTO,
                                Collectors.toList())
                ));
    }

    /**
     * To delete given player on a given position 
     * @param position
     * @param playerName
     * @param playerUniqueNumber
     * @return deleted player
     */
    public Optional<PlayerDTO> deletePlayerOnAPosition(String position, String playerName, Integer playerUniqueNumber) {

        Player player = retrievePlayer(playerName,playerUniqueNumber);

        List<DepthChart> depthChartList = retrieveDepthChartForPosition(position);

        Optional<DepthChart> depthChartData =  depthChartList.stream()
                .filter(dcd ->
                        dcd.getPlayer().getUniqueTeamNumber().equals(player.getUniqueTeamNumber()))
                .findFirst();

        if(depthChartData.isEmpty()){
            return Optional.empty();
        }

        /**
         * I have not moved the position_depth by 1 as it is not stated in the requirement
         */
        depthChartRepository.deleteById(depthChartData.get().getDepthChartDataId());
        return Optional.of(mapToPLayerDTO(depthChartData.get()));

    }

    /**
     * retrieve a player from db for a given name and unique number
     * @param playerName
     * @param playerUniqueNumber
     * @return player
     */
    private Player retrievePlayer(String playerName, Integer playerUniqueNumber) {
        Optional<Player> player = playerRepository.findByPlayerNameAndUniqueTeamNumber(playerName, playerUniqueNumber);
        if (player.isEmpty()) {
            log.error("Player with name : {} and unique Team number : {} is not found ", playerName, playerUniqueNumber);
            throw new PlayerNotFoundException("Player with name : " + playerName + " with unique Team number : "+playerUniqueNumber+"  not found");
        }
        return player.get();
    }

    /**
     * To retrieve depth chart of a given position
     * @param position
     * @return list of depth charts on a given position
     */
    private List<DepthChart> retrieveDepthChartForPosition(String position){
        List<DepthChart> depthChartList = depthChartRepository
                .findByPosition(position);

        if (depthChartList.isEmpty()) {
            log.error("Position : {} not found in Depth Chart", position);
            throw new PositionNotFoundException("Position : " + position + " not found in Depth Chart");
        }
        return depthChartList;
    }
}
