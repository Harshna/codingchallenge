package com.sportsbet.codingchallenge.depthchart.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record DepthChartDTO(

        Long id,

        @NotBlank(message = "Position is required")
        String position,

        @PositiveOrZero(message = "Position Depth cannot be negative")
        Integer positionDepth,

        @Valid
        @NotNull(message = "Player is required")
        PlayerDTO player)
{}
