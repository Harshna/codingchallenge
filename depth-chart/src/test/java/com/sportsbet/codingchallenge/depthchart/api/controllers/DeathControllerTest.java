package com.sportsbet.codingchallenge.depthchart.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportsbet.codingchallenge.depthchart.api.DepthChartDTO;
import com.sportsbet.codingchallenge.depthchart.api.PlayerDTO;
import com.sportsbet.codingchallenge.depthchart.api.exception.DepthChartCreationException;
import com.sportsbet.codingchallenge.depthchart.api.exception.PlayerNotFoundException;
import com.sportsbet.codingchallenge.depthchart.api.exception.PositionNotFoundException;
import com.sportsbet.codingchallenge.depthchart.api.service.DepthChartService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebMvcTest(DepthChartController.class)
public class DeathControllerTest {

    @Autowired(required = true)
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DepthChartService depthChartService;

    @Test
    public void testCreateDepthChartData_Success() throws Exception {

        PlayerDTO playerDTO = new PlayerDTO(null,"Tom Brady",10);

        var requestedDepthChartDTO = new DepthChartDTO(null,"LWR",1,playerDTO);
        var responseDepthChartDTO = new DepthChartDTO(1l,"LWR",1,playerDTO);

        when(depthChartService.createDepthChartData(requestedDepthChartDTO)).thenReturn(responseDepthChartDTO);

        mockMvc.perform(post("/api/v1/depth-charts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestedDepthChartDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.position").value("LWR"))
                .andExpect(jsonPath("$.positionDepth").value(1))
                .andExpect(jsonPath("$.player.playerName").value("Tom Brady"))
                .andExpect(jsonPath("$.player.uniqueTeamNumber").value(10));;
    }

    @Test
    public void testCreateDepthChartData_InvalidPosition_Failure() throws Exception {

        PlayerDTO playerDTO = new PlayerDTO(null,"Tom Brady",10);

        var invalidRequestedDepthChartDTO = new DepthChartDTO(null,"",1,playerDTO);

        mockMvc.perform(post("/api/v1/depth-charts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestedDepthChartDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateDepthChartData_NullPLayer_Failure() throws Exception {

        var invalidRequestedDepthChartDTO = new DepthChartDTO(null,"",1,null);

        mockMvc.perform(post("/api/v1/depth-charts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestedDepthChartDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateDepthChartData_PlayerNotFound404_Failure() throws Exception {

        PlayerDTO playerDTO = new PlayerDTO(null,"Tom Brady",10);

        var invalidRequestedDepthChartDTO = new DepthChartDTO(null,"QB",1,playerDTO);
        when(depthChartService.createDepthChartData(any(DepthChartDTO.class))).thenThrow(new PlayerNotFoundException("Player not found"));

        mockMvc.perform(post("/api/v1/depth-charts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestedDepthChartDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateDepthChartData_PositionNotFound404_Failure() throws Exception {

        PlayerDTO playerDTO = new PlayerDTO(null,"Tom Brady",10);

        var invalidRequestedDepthChartDTO = new DepthChartDTO(null,"QB",1,playerDTO);
        when(depthChartService.createDepthChartData(any(DepthChartDTO.class))).thenThrow(new PositionNotFoundException("Position not found"));

        mockMvc.perform(post("/api/v1/depth-charts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestedDepthChartDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateDepthChartData_DepthChartCreationException500_Failure() throws Exception {

        PlayerDTO playerDTO = new PlayerDTO(null,"Tom Brady",10);

        var invalidRequestedDepthChartDTO = new DepthChartDTO(null,"QB",1,playerDTO);
        when(depthChartService.createDepthChartData(any(DepthChartDTO.class))).thenThrow(new DepthChartCreationException("Depth Chart could not be created"));

        mockMvc.perform(post("/api/v1/depth-charts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestedDepthChartDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testGetBackups_Success() throws Exception {

        PlayerDTO playerDTO1 = new PlayerDTO(null,"Richard Williams",9);
        PlayerDTO playerDTO2 = new PlayerDTO(null,"Scott Miller",10);

        List<PlayerDTO> backUpPlayers = List.of(playerDTO1,playerDTO2);

        when(depthChartService.retrieveBackupsForAPLayerOnAPosition("QB","Tom Brady",12)).thenReturn(backUpPlayers);

        mockMvc.perform(get("/api/v1/depth-charts/QB/players/back-ups")
                        .param("playerName", "Tom Brady")
                        .param("playerUniqueNumber", "12")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].playerName").value("Richard Williams"))
                .andExpect(jsonPath("$[0].uniqueTeamNumber").value(9))
                .andExpect(jsonPath("$[1].playerName").value("Scott Miller"))
                .andExpect(jsonPath("$[1].uniqueTeamNumber").value(10));
    }

    @Test
    public void testGetBackups_PlayerNotFound_Failure() throws Exception {

        when(depthChartService.retrieveBackupsForAPLayerOnAPosition("QB","Tom Brady",12)).thenThrow(new PlayerNotFoundException("Player not found"));

        mockMvc.perform(get("/api/v1/depth-charts/QB/players/back-ups")
                        .param("playerName", "Tom Brady")
                        .param("playerUniqueNumber", "12")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetBackups_PositionNotFound_Failure() throws Exception {

        when(depthChartService.retrieveBackupsForAPLayerOnAPosition("QB","Tom Brady",12)).thenThrow(new PositionNotFoundException("Position not found"));

        mockMvc.perform(get("/api/v1/depth-charts/QB/players/back-ups")
                        .param("playerName", "Tom Brady")
                        .param("playerUniqueNumber", "12")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetFullDepthChart_Success() throws Exception {

        when(depthChartService.retrieveBackupsForAPLayerOnAPosition("QB","Tom Brady",12)).thenThrow(new PositionNotFoundException("Position not found"));

        mockMvc.perform(get("/api/v1/depth-charts/full-depth-chart")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeletePlayer_Success() throws Exception {

        when(depthChartService.deletePlayerOnAPosition("QB","Tom Brady",12)).thenReturn(Optional.of(new PlayerDTO(1l,"Richard Williams",9)));

        mockMvc.perform(delete("/api/v1/depth-charts/QB/players")
                        .param("playerName", "Tom Brady")
                        .param("playerUniqueNumber", "12"))
                .andExpect(status().isOk());
    }
}
