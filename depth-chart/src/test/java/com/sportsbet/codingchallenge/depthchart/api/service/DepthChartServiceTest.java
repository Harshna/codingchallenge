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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DepthChartServiceTest {
    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private DepthChartRepository depthChartRepository;

    @InjectMocks
    private DepthChartServiceImpl depthChartService;


    @Test
    void testCreateDepthChartData_success() {
        PlayerDTO playerDTO = new PlayerDTO(null,"Tom Brady",10);

        Player savedPlayer = new Player("Tom Brady",10);
        savedPlayer.setId(1l);

        DepthChartDTO inputtDepthChartDTO = new DepthChartDTO(null,"QB",3,playerDTO);

        DepthChart savedDepthChart = new DepthChart("QB",3,savedPlayer);

        var existingDepthChartList = List.of(new DepthChart("QB",1,new Player("Tom Brady",101)),
                new DepthChart("QB",2,new Player("Tom Brady",20)));

        when(playerRepository.findByPlayerNameAndUniqueTeamNumber(playerDTO.playerName(), playerDTO.uniqueTeamNumber())).thenReturn(Optional.of(savedPlayer));

        when(depthChartRepository.findByPosition(inputtDepthChartDTO.position())).thenReturn(existingDepthChartList);

        when(depthChartRepository.save(any(DepthChart.class))).thenReturn(savedDepthChart);

        DepthChartDTO result = depthChartService.createDepthChartData(inputtDepthChartDTO);

        assertNotNull(result);
        assertEquals("QB"  ,result.position());
        assertEquals("Tom Brady",result.player().playerName());
        assertEquals(10,result.player().uniqueTeamNumber());
        assertEquals(3,result.positionDepth());

    }

    @Test
    void testCreateDepthChartData_playerNotFoundFailure() {

        PlayerDTO playerDTO = new PlayerDTO(null,"Tom Brady",10);
        DepthChartDTO inputtDepthChartDTO = new DepthChartDTO(null,"QB",3,playerDTO);

        when(playerRepository.findByPlayerNameAndUniqueTeamNumber("Tom Brady",10)).thenReturn(Optional.empty());
        PlayerNotFoundException ex = assertThrows(PlayerNotFoundException.class, ()->depthChartService.createDepthChartData(inputtDepthChartDTO));

        assertEquals("Player with name : " + playerDTO.playerName() + " with unique Team number : "+playerDTO.uniqueTeamNumber()+"  not found", ex.getMessage());

    }

    @Test
    void testCreateDepthChartData_duplicatePLayerPositionFailure() {
        PlayerDTO playerDTO = new PlayerDTO(null,"Tom Brady",10);

        Player savedPlayer = new Player("Tom Brady",10);
        savedPlayer.setId(1l);

        DepthChartDTO inputtDepthChartDTO = new DepthChartDTO(null,"QB",3,playerDTO);

        DepthChart savedDepthChart = new DepthChart("QB",3,savedPlayer);

        var existingDepthChartList = List.of(new DepthChart("QB",1,new Player("Tom Brady",10)),
                new DepthChart("QB",2,new Player("Tom Brady",20)));

        when(playerRepository.findByPlayerNameAndUniqueTeamNumber(playerDTO.playerName(), playerDTO.uniqueTeamNumber())).thenReturn(Optional.of(savedPlayer));

        when(depthChartRepository.findByPosition(inputtDepthChartDTO.position())).thenReturn(existingDepthChartList);

        DepthChartCreationException ex = assertThrows(DepthChartCreationException.class,
                ()->depthChartService.createDepthChartData(inputtDepthChartDTO));

        assertTrue(ex.getMessage().contains("Tom Brady"));

    }

    @Test
    void createDepthChartData_withExplicitPositionDepthSuccess() {
        // given
        Player savedPlayer = new Player("Tom Brady",12);
        savedPlayer.setId(1l);

        PlayerDTO playerDTO = new PlayerDTO(null,"Tom Brady", 12);
        // request to insert at depth 1
        DepthChartDTO input = new DepthChartDTO(null, "QB", 1, playerDTO);

        when(playerRepository.findByPlayerNameAndUniqueTeamNumber("Tom Brady",12)).thenReturn(Optional.of(savedPlayer));

        // existing entries with different players
        Player otherPlayer1 = new Player("Scott MIller",99);
        otherPlayer1.setId(2L);


        DepthChart existing1 = new DepthChart("QB",1,otherPlayer1);
        existing1.setDepthChartDataId(100L);

        Player otherPlayer2 = new Player("Richard Williams",88);
        otherPlayer2.setId(3L);

        DepthChart existing2 = new DepthChart("QB",2,otherPlayer2);
        existing1.setDepthChartDataId(200L);

        when(depthChartRepository.findByPosition("QB")).thenReturn(List.of(existing1, existing2));

        // the new DepthChart being saved

        DepthChart savedDepthChart = new DepthChart("QB",1,savedPlayer);
        savedDepthChart.setDepthChartDataId(200L);

        when(depthChartRepository.save(any(DepthChart.class))).thenReturn(savedDepthChart);

        // when
        DepthChartDTO result = depthChartService.createDepthChartData(input);

        // then: verify returned DTO mapping
        assertNotNull(result);
        assertEquals(200L, result.id());
        assertEquals("QB", result.position());
        assertEquals(1, result.positionDepth());
        assertEquals("Tom Brady", result.player().playerName());

    }

    @Test
    void createDepthChartData_withNULLPositionDepthSuccess_shouldAppendAtEnd() {
        Player savedPlayer = new Player("Tom Brady",12);
        savedPlayer.setId(1l);

        PlayerDTO playerDTO = new PlayerDTO(null,"Tom Brady", 12);
        // request to insert at depth 1
        DepthChartDTO input = new DepthChartDTO(null, "QB", null, playerDTO);

        when(playerRepository.findByPlayerNameAndUniqueTeamNumber("Tom Brady",12)).thenReturn(Optional.of(savedPlayer));

        // existing entries with different players
        Player otherPlayer1 = new Player("Scott MIller",99);
        otherPlayer1.setId(2L);

        DepthChart existing1 = new DepthChart("QB",1,otherPlayer1);
        existing1.setDepthChartDataId(100L);

        Player otherPlayer2 = new Player("Richard Williams",88);
        otherPlayer2.setId(3L);

        DepthChart existing2 = new DepthChart("QB",2,otherPlayer2);
        existing1.setDepthChartDataId(200L);

        when(depthChartRepository.findByPosition("QB")).thenReturn(List.of(existing1, existing2));

        DepthChart savedDepthChart = new DepthChart("QB",3,savedPlayer);
        savedDepthChart.setDepthChartDataId(300L);

        when(depthChartRepository.save(any(DepthChart.class))).thenReturn(savedDepthChart);

        DepthChartDTO result = depthChartService.createDepthChartData(input);

        assertEquals("QB", result.position());
        assertEquals(3, result.positionDepth());
    }

    @Test
    void testRetrieveBackupsForAPLayerOnAPosition_playerNotFoundFailure() {

        String position = "QB";
        String playerName = "Tom Brady";
        Integer playerUniqueNumber = 12;


        when(playerRepository.findByPlayerNameAndUniqueTeamNumber(playerName,playerUniqueNumber)).thenReturn(Optional.empty());
        PlayerNotFoundException ex = assertThrows(PlayerNotFoundException.class, ()->depthChartService.retrieveBackupsForAPLayerOnAPosition(position,playerName,playerUniqueNumber));

        assertEquals("Player with name : " + playerName + " with unique Team number : "+playerUniqueNumber+"  not found", ex.getMessage());

    }

    @Test
    void testRetrieveBackupsForAPLayerOnAPosition_DepthChartDoesNotHavePositionFailure() {

        String position = "QB";
        String playerName = "Tom Brady";
        Integer playerUniqueNumber = 12;

        Player savedPlayer = new Player("Tom Brady",12);
        savedPlayer.setId(1l);

        when(playerRepository.findByPlayerNameAndUniqueTeamNumber(playerName, playerUniqueNumber)).thenReturn(Optional.of(savedPlayer));

        when(depthChartRepository.findByPosition(position)).thenReturn(Collections.emptyList());

        PositionNotFoundException ex = assertThrows(PositionNotFoundException.class, ()->depthChartService.retrieveBackupsForAPLayerOnAPosition(position,playerName,playerUniqueNumber));

        assertEquals("Position : " + position + " not found in Depth Chart", ex.getMessage());

    }

    @Test
    void testRetrieveBackupsForAPLayerOnAPosition_EmptyListWhenNOBackUpSuccess() {

        String position = "QB";
        String playerName = "Tom Brady";
        Integer playerUniqueNumber = 12;

        Player savedPlayer = new Player("Tom Brady",12);
        savedPlayer.setId(1l);

        when(playerRepository.findByPlayerNameAndUniqueTeamNumber(playerName, playerUniqueNumber)).thenReturn(Optional.of(savedPlayer));

        // existing entries with different players
        Player otherPlayer1 = new Player("Scott MIller",99);
        otherPlayer1.setId(2L);

        DepthChart existing1 = new DepthChart("QB",1,otherPlayer1);
        existing1.setDepthChartDataId(100L);

        Player otherPlayer2 = new Player("Tom Brady",12);
        otherPlayer2.setId(3L);

        DepthChart existing2 = new DepthChart("QB",2,otherPlayer2);
        existing1.setDepthChartDataId(200L);

        when(depthChartRepository.findByPosition("QB")).thenReturn(List.of(existing1, existing2));

        List<PlayerDTO> result = depthChartService.retrieveBackupsForAPLayerOnAPosition(position,playerName,playerUniqueNumber);

        //Expecting an empty list
        assertEquals(0, result.size());
    }

    @Test
    void testRetrieveBackupsForAPLayerOnAPosition_EmptyListWhenPLayerNotListedOnDepthChartForThatPositionSuccess() {

        String position = "QB";
        String playerName = "Tom Brady";
        Integer playerUniqueNumber = 12;

        Player savedPlayer = new Player("Tom Brady",12);
        savedPlayer.setId(1l);

        when(playerRepository.findByPlayerNameAndUniqueTeamNumber(playerName, playerUniqueNumber)).thenReturn(Optional.of(savedPlayer));

        // existing entries with different players
        Player otherPlayer1 = new Player("Scott MIller",99);
        otherPlayer1.setId(2L);

        DepthChart existing1 = new DepthChart("QB",1,otherPlayer1);
        existing1.setDepthChartDataId(100L);

        Player otherPlayer2 = new Player("Richard Williams",9);
        otherPlayer2.setId(3L);

        DepthChart existing2 = new DepthChart("QB",2,otherPlayer2);
        existing1.setDepthChartDataId(200L);

        when(depthChartRepository.findByPosition("QB")).thenReturn(List.of(existing1, existing2));

        List<PlayerDTO> result = depthChartService.retrieveBackupsForAPLayerOnAPosition(position,playerName,playerUniqueNumber);

        //Expecting an empty list
        assertEquals(0, result.size());
    }

    @Test
    void testRetrieveBackupsForAPLayerOnAPosition_AllBackUpPLayersListedOnDepthChartForThatPositionSuccess() {

        String position = "QB";
        String playerName = "Tom Brady";
        Integer playerUniqueNumber = 12;

        Player savedPlayer = new Player("Tom Brady",12);
        savedPlayer.setId(1l);

        when(playerRepository.findByPlayerNameAndUniqueTeamNumber(playerName, playerUniqueNumber)).thenReturn(Optional.of(savedPlayer));

        // existing entries with different players
        DepthChart existing = new DepthChart("QB",1,savedPlayer);
        existing.setDepthChartDataId(100L);

        Player otherPlayer1 = new Player("Scott MIller",99);
        otherPlayer1.setId(2L);

        DepthChart existing1 = new DepthChart("QB",2,otherPlayer1);
        existing1.setDepthChartDataId(200L);

        Player otherPlayer2 = new Player("Richard Williams",9);
        otherPlayer2.setId(3L);

        DepthChart existing2 = new DepthChart("QB",3,otherPlayer2);
        existing1.setDepthChartDataId(300L);

        when(depthChartRepository.findByPosition("QB")).thenReturn(List.of(existing, existing1, existing2));

        List<PlayerDTO> result = depthChartService.retrieveBackupsForAPLayerOnAPosition(position,playerName,playerUniqueNumber);

        assertEquals(2, result.size());
        assertEquals("Scott MIller",result.get(0).playerName());
        assertEquals("Richard Williams",result.get(1).playerName());
    }

    @Test
    void testRetrieveFullDepthChart_Success() {

        Player savedPlayer = new Player("Tom Brady",12);
        savedPlayer.setId(1l);

        // existing entries with different players

        DepthChart existing = new DepthChart("QB",1,savedPlayer);
        existing.setDepthChartDataId(100L);

        Player otherPlayer1 = new Player("Scott Miller",99);
        otherPlayer1.setId(2L);

        DepthChart existing1 = new DepthChart("LWR",1,otherPlayer1);
        existing1.setDepthChartDataId(200L);

        Player otherPlayer2 = new Player("Richard Williams",9);
        otherPlayer2.setId(3L);

        DepthChart existing2 = new DepthChart("LWR",2,otherPlayer2);
        existing1.setDepthChartDataId(300L);

        when(depthChartRepository.findAll()).thenReturn(List.of(existing, existing1, existing2));

        Map<String,List<PlayerDTO>> result = depthChartService.retrieveFullDepthChart();

        assertEquals(2, result.size());
        assertTrue(result.containsKey("QB"));
        assertTrue(result.containsKey("LWR"));

        List<PlayerDTO> playerQBDTOS = result.get("QB");
        assertEquals("Tom Brady",playerQBDTOS.get(0).playerName());

        List<PlayerDTO> playerLWRDTOS = result.get("LWR");
        assertEquals("Scott Miller",playerLWRDTOS.get(0).playerName());
        assertEquals("Richard Williams",playerLWRDTOS.get(1).playerName());
    }

    @Test
    void testDeletePlayerOnAPosition_PlayerNotFound_Failure() {
        String position = "QB";
        String playerName = "Tom Brady";
        Integer playerUniqueNumber = 12;

        when(playerRepository.findByPlayerNameAndUniqueTeamNumber(playerName,playerUniqueNumber)).thenReturn(Optional.empty());
        PlayerNotFoundException ex = assertThrows(PlayerNotFoundException.class, ()->depthChartService.deletePlayerOnAPosition(position,playerName,playerUniqueNumber));

        assertEquals("Player with name : " + playerName + " with unique Team number : "+playerUniqueNumber+"  not found", ex.getMessage());

    }

    @Test
    void testDeletePlayerOnAPosition_PositionNotFound_Failure() {

        String position = "QB";
        String playerName = "Tom Brady";
        Integer playerUniqueNumber = 12;

        Player savedPlayer = new Player("Tom Brady",12);
        savedPlayer.setId(1l);

        when(playerRepository.findByPlayerNameAndUniqueTeamNumber(playerName, playerUniqueNumber)).thenReturn(Optional.of(savedPlayer));

        when(depthChartRepository.findByPosition(position)).thenReturn(Collections.emptyList());

        PositionNotFoundException ex = assertThrows(PositionNotFoundException.class, ()->depthChartService.deletePlayerOnAPosition(position,playerName,playerUniqueNumber));

        assertEquals("Position : " + position + " not found in Depth Chart", ex.getMessage());

    }

    @Test
    void testDeletePlayerOnAPosition_ReturnEmptyWhenPlayerIsNotFoundOnThatPosition_Success() {

        String position = "QB";
        String playerName = "Tom Brady";
        Integer playerUniqueNumber = 12;

        Player savedPlayer = new Player("Tom Brady",12);
        savedPlayer.setId(1l);

        when(playerRepository.findByPlayerNameAndUniqueTeamNumber(playerName, playerUniqueNumber)).thenReturn(Optional.of(savedPlayer));

        // existing entries with different players
        Player otherPlayer1 = new Player("Scott MIller",99);
        otherPlayer1.setId(2L);

        DepthChart existing1 = new DepthChart("QB",1,otherPlayer1);
        existing1.setDepthChartDataId(100L);

        Player otherPlayer2 = new Player("Richard Williams",9);
        otherPlayer2.setId(3L);

        DepthChart existing2 = new DepthChart("QB",2,otherPlayer2);
        existing1.setDepthChartDataId(200L);

        when(depthChartRepository.findByPosition("QB")).thenReturn(List.of(existing1, existing2));

        var result = depthChartService.deletePlayerOnAPosition(position,playerName,playerUniqueNumber);

        assertTrue(result.isEmpty());
    }

    @Test
    void testDeletePlayerOnAPosition_Success() {

        String position = "QB";
        String playerName = "Tom Brady";
        Integer playerUniqueNumber = 12;

        Player savedPlayer = new Player("Tom Brady",12);
        savedPlayer.setId(1l);

        when(playerRepository.findByPlayerNameAndUniqueTeamNumber(playerName, playerUniqueNumber)).thenReturn(Optional.of(savedPlayer));

        // existing entries with different players
        Player otherPlayer1 = new Player("Scott MIller",99);
        otherPlayer1.setId(2L);

        DepthChart existing1 = new DepthChart("QB",1,otherPlayer1);
        existing1.setDepthChartDataId(100L);

        Player otherPlayer2 = new Player("Richard Williams",9);
        otherPlayer2.setId(3L);

        DepthChart existing2 = new DepthChart("QB",2,otherPlayer2);
        existing1.setDepthChartDataId(200L);

        DepthChart existing3 = new DepthChart("QB",3,savedPlayer);
        existing1.setDepthChartDataId(300L);

        when(depthChartRepository.findByPosition("QB")).thenReturn(List.of(existing1, existing2, existing3));

        var result = depthChartService.deletePlayerOnAPosition(position,playerName,playerUniqueNumber);

        assertFalse(result.isEmpty());
        assertEquals("Tom Brady",result.get().playerName());
        assertEquals(12,result.get().uniqueTeamNumber());
    }
}
