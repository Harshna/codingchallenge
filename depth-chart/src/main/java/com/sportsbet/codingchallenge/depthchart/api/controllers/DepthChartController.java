package com.sportsbet.codingchallenge.depthchart.api.controllers;

import com.sportsbet.codingchallenge.depthchart.api.DepthChartDTO;
import com.sportsbet.codingchallenge.depthchart.api.PlayerDTO;
import com.sportsbet.codingchallenge.depthchart.api.service.DepthChartService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/depth-charts")
@Slf4j
public class DepthChartController {


    private DepthChartService depthChartService;
    public DepthChartController(DepthChartService depthChartService) {
        this.depthChartService = depthChartService;
    }


    @PostMapping
    @Operation(summary="Create a Depth Chart for a player on a position")
    public ResponseEntity<DepthChartDTO> createDepthChartData(@Valid @RequestBody DepthChartDTO depthChartDTO) {
        log.info("Received request to create depth chart data");
        DepthChartDTO savedDepthChartDTO = this.depthChartService.createDepthChartData(depthChartDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedDepthChartDTO);
    }

    @GetMapping("/{position}/players/back-ups")
    @Operation(summary="Get backups for a given player on a position")
    public ResponseEntity<List<PlayerDTO>> getBackups(@PathVariable String position, @RequestParam(required = false) String playerName, @RequestParam Integer playerUniqueNumber) {
        return ResponseEntity.ok(depthChartService.retrieveBackupsForAPLayerOnAPosition(position,playerName, playerUniqueNumber));

    }

    @GetMapping("/full-depth-chart")
    @Operation(summary="Get full depth charts for every position")
    public ResponseEntity<Map<String,List<PlayerDTO>>> getFullDepthChart() {
        return ResponseEntity.ok(depthChartService.retrieveFullDepthChart());
    }


    @DeleteMapping("/{position}/players")
    @Operation(summary="Delete a player on a position in a depth chart")
    public ResponseEntity<PlayerDTO> deletePlayer(@PathVariable String position, @RequestParam String playerName, @RequestParam Integer playerUniqueNumber) {
        return ResponseEntity.ok(depthChartService.deletePlayerOnAPosition(position, playerName,playerUniqueNumber).orElse(null));
    }

}
